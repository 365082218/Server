package idevgame.meteor.utils;

import java.io.File;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * svn 婢跺嫮鎮婃禍瀣剰
 * 
 * @author chen 2014楠烇拷11閺堬拷17閺冿拷
 * 
 */
public class SVNUtils {

	/**
	 * 鐎电厧鍤挧鍕爱
	 * @param url svn 鏉╃偞甯�
	 * @param destPath 鐎电厧鍤惄顔肩秿
	 * @param userName 閻€劍鍩涢崥锟�
	 * @param password 鐎靛棛鐖�
	 * @throws SVNException 
	 */
	public static void export(String url, String destPath, String userName,
			String password) throws SVNException {
		SVNRepository repository = null;
		// initiate the reporitory from the url
		repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
		// create authentication data
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(userName, password);
		repository.setAuthenticationManager(authManager);
		// output some data to verify connection
		System.out.println("Repository Root: "
				+ repository.getRepositoryRoot(true));
		System.out.println("Repository UUID: "
				+ repository.getRepositoryUUID(true));
		// need to identify latest revision
		long latestRevision = repository.getLatestRevision();
		System.out.println("Repository Latest Revision: " + latestRevision);
		// create client manager and set authentication
		SVNClientManager ourClientManager = SVNClientManager.newInstance();
		ourClientManager.setAuthenticationManager(authManager);
		// use SVNUpdateClient to do the export
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		updateClient.doExport(repository.getLocation(), new File(destPath),
				SVNRevision.create(latestRevision),
				SVNRevision.create(latestRevision), null, true,
				SVNDepth.INFINITY);
	}
	
	public static void main(String[] args) {
		
		
		final String url = "svn://10.6.8.203/world2/trunk/docs/http_res/client";
		final String destPath = "c:/temp/svntest";
		String username = "gengqing";
		String password = "gengqing8989";
		try {
			SVNUtils.export(url, destPath, username, password);
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
