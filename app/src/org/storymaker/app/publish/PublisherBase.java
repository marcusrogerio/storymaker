package org.storymaker.app.publish;

import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;

import org.storymaker.app.R;
import org.storymaker.app.StoryMakerApp;
import org.storymaker.app.model.Auth;
import org.storymaker.app.model.AuthTable;
import org.storymaker.app.model.Job;
import org.storymaker.app.model.JobTable;
import org.storymaker.app.model.Project;
import org.storymaker.app.model.PublishJob;
import org.storymaker.app.server.ServerManager;
import io.scal.secureshareui.controller.SiteController;
import io.scal.secureshareui.controller.ZTSiteController;

public abstract class PublisherBase {
    private final static String TAG = "PublisherBase";
    protected PublishController mController;
    protected PublishJob mPublishJob;
    protected Context mContext;
    
    public PublisherBase(Context context, PublishController publishController, PublishJob publishJob) {
        mContext = context;
        mController = publishController;
        mPublishJob = publishJob;
    }
    
    public abstract void startRender();
    
    public abstract void startUpload();
	
//	public abstract void jobSucceeded(Job job);

    public abstract String getEmbed(Job job);

    public abstract String getResultUrl(Job job);

	public String publishToStoryMaker() {
	    Job job = getPreferredUploadJob();
	    
	    Project project = mPublishJob.getProject();
	    String title = project.getTitle();
	    String desc = project.getDescription(); 
	    String mediaEmbed = getEmbed(job);
	    String[] categories = project.getCategories();
	    String medium =  getMedium();
	    String mediaService = job.getSite();
	    String mediaGuid = job.getResult(); // TODO get the id from the preferred job to publish to facebook

        /*
        Log.d("PUBLISH", "TITLE: " + title);
        Log.d("PUBLISH", "DESC: " + desc);
        Log.d("PUBLISH", "EMBED: " + mediaEmbed);
        Log.d("PUBLISH", "CATEGORIES: " + Arrays.toString(categories));
        Log.d("PUBLISH", "MEDIUM: " + medium);
        Log.d("PUBLISH", "SERVICE: " + mediaService);
        Log.d("PUBLISH", "GUID: " + mediaGuid);
        */

	    try {
            String ret = publishToStoryMaker(title, desc, mediaEmbed, categories, medium, mediaService, mediaGuid);
            return ret;
        } /*catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlRpcFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/ catch (IOException ioe) {
            ioe.printStackTrace();
        }
	    return null;
	}
	
	private Job getPreferredUploadJob() {
	    // TODO this should chose from all the selected sites for this publish the preferred site from this list: youtube, facebook, ...
	    for (Job job: mPublishJob.getJobsAsList()) {
	        if (job.isType(JobTable.TYPE_UPLOAD)) {
				job.setSpec(getMedium());
				return job;
	        }
	    }
	    return null;
	}
	
	public String getMedium() {
	    if (mPublishJob.getProject().getStoryType() == Project.STORY_TYPE_AUDIO) {
	        return ServerManager.CUSTOM_FIELD_MEDIUM_AUDIO;
	    } else if (mPublishJob.getProject().getStoryType() == Project.STORY_TYPE_VIDEO) {
            return ServerManager.CUSTOM_FIELD_MEDIUM_VIDEO;
        } else if (mPublishJob.getProject().getStoryType() == Project.STORY_TYPE_PHOTO) {
            return ServerManager.CUSTOM_FIELD_MEDIUM_PHOTO;
        } else {
            return "";
        }
	}
    
    public String publishToStoryMaker(String title, String desc, String mediaEmbed, String[] categories, String medium, String mediaService, String mediaGuid) throws IOException // MalformedURLException, XmlRpcFault
    {
        ServerManager sm = StoryMakerApp.getServerManager();
        sm.setContext(mContext);

//        Message msgStatus = mHandlerPub.obtainMessage(888);
//        msgStatus.getData().putString("status",
//                getActivity().getString(R.string.uploading_to_storymaker));
//        mHandlerPub.sendMessage(msgStatus);
        
        mController.publishJobProgress(mPublishJob, 0, mContext.getString(R.string.publishing_to_storymakerorg));
        // split out embed
        // String descWithMedia = desc + "\n\n" + mediaEmbed;

        // now returning url directly
        //String postId = sm.post(title, desc, mediaEmbed, categories, medium, mediaService, mediaGuid);
        mController.publishJobProgress(mPublishJob, 0.5f, mContext.getString(R.string.publishing_to_storymakerorg));
        //String urlPost = sm.getPostUrl(postId);
        String urlPost = sm.post(title, desc, mediaEmbed, categories, medium, mediaService, mediaGuid);

        // FIXME make this async and put this in the callback
        // FIXME store the final published url in the project table?
        publishToStoryMakerSucceeded(urlPost);
        
        return urlPost;
    }
    
    public void publishToStoryMakerSucceeded(String url) {
        mController.publishJobProgress(mPublishJob, 1, mContext.getString(R.string.published_to_storymaker));
        publishSucceeded(url);
    }
    
    public void publishToStoryMakerFailed(Exception exception, int errorCode, String errorMessage) {
        publishFailed(exception, errorCode, errorMessage);
    }
	
    public void publishSucceeded(String url) {
//        mPublishJob.setResult(url); // FIXME implement this
        mPublishJob.setFinishedAtNow();
        mPublishJob.save();
        mController.publishJobSucceeded(mPublishJob, url);
    }

    public void publishFailed(Exception exception, int errorCode, String errorMessage) {
        mController.publishJobFailed(mPublishJob, exception, errorCode, errorMessage);
    }
    
    public void jobSucceeded(Job job) {
        Log.d(TAG, "jobSucceeded: " + job);
        if (job.isType(JobTable.TYPE_RENDER)) {
            // since the user must now initiate upload, we just stop this publishjob now and wait
//            mController.publishJobSucceeded(mPublishJob);
		} else if (job.isType(JobTable.TYPE_UPLOAD)) {
			String publishToStoryMaker = mPublishJob.getMetadata().get(SiteController.VALUE_KEY_PUBLISH_TO_STORYMAKER);
			if (publishToStoryMaker != null && publishToStoryMaker.equals("true")) {
				//Auth auth = (new AuthTable()).getAuthDefault(mContext, Auth.SITE_STORYMAKER);
                Auth auth = (new AuthTable()).getAuthDefault(mContext, ZTSiteController.SITE_KEY);
				if (auth != null) {
					publishToStoryMaker();  // fail on null result?
				} else {
					mController.publishJobFailed(mPublishJob, null, 78268832, mContext.getString(R.string.you_are_not_signed_into_storymakerorg)); // FIXME do this nicer!
				}
			} else {
                publishSucceeded(getResultUrl(job));
			}
		}
	}
    
    public void jobFailed(Job job, Exception exception, int errorCode, String errorMessage) {
        Log.d(TAG, "jobFailed: " + job);
        mController.publishJobFailed(mPublishJob, exception, errorCode, errorMessage);
    }

    /**
     * 
     * @param job
     * @param progress 0 to 1
     * @param message Message displayed to user
     */
    public void jobProgress(Job job, float progress, String message) {
        mController.publishJobProgress(mPublishJob, progress, message);
    }
}
