import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
/*
 * A class that automatically pulls for
 * local repositories from the github
 * URL specified in the HEAD Ref
 * 
 */
public class Puller {

	FileWalker fw;

	private ArrayList<Repository> repos;
	private ArrayList<Git> gits;

	public static void main(String[] args) {
		GitRepoBuilder.init();

		Puller p = new Puller();
		p.pullAll();
		System.out.println("Done pulling");
	}

	public Puller() {
		// Make sure we haven't called prematurely
		if (GitRepoBuilder.getrepositoryGits() == null
				|| GitRepoBuilder.getrepositoryRepos() == null) {
			System.out
					.println("Likely that program has not been initialized. Closing!");
			return;
		}
		// Make sure repos exist
		if (!GitRepoBuilder.getrepositoryGits().isEmpty()
				&& !GitRepoBuilder.getrepositoryRepos().isEmpty()) {
			gits = GitRepoBuilder.getrepositoryGits();
			repos = GitRepoBuilder.getrepositoryRepos();
		}
		// Case of no git construction
		else if (!GitRepoBuilder.getrepositoryRepos().isEmpty()) {
			repos = GitRepoBuilder.getrepositoryRepos();

			gits.clear();
			for (Repository r : repos) {
				gits.add(new Git(r));
			}
			GitRepoBuilder.setrepositoryGits(gits);
		}
		// No repos on computer
		else {
			System.out.println("Do not have any repos. Closing!");
		}
	}

	// Basically just calls a pull command for each git object
	public void pullAll() {
		for (Git g : gits) {
			PullThread pt = new PullThread(g);
			pt.start();
		}
	}

	public void pullSingle(Git g) {
		try {
			g.pull().setCredentialsProvider(Core.getCreds()).call();
		} catch (TransportException e) {
			System.out.println("Not allowed access to repository");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pullSingle(String s) {
		try {

			Git g = new Git(new FileRepository(s));
			g.pull().setCredentialsProvider(Core.getCreds()).call();
			g.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class PullThread extends Thread {
	private Git g;

	public PullThread(Git g) {
		this.g = g;
	}

	public void run() {
		try {
			g.pull().setCredentialsProvider(Core.getCreds()).call();

		} catch (TransportException e) {
			System.out.println("Not allowed access to repository");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
