/* copyright the Déductions Project
under GNU Lesser General Public License
http://www.gnu.org/licenses/lgpl.html
$Id$
 */
package eulergui.fcm;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import deductions.Namespaces;


import eulergui.gui.main.ProjectGUI;
import eulergui.interfaces.AbstractProjectListener;
import eulergui.interfaces.ProjectListener;
import eulergui.project.N3Source;
import eulergui.project.Project;
import eulergui.project.ProjectFactory;

/** For Case Based Fuzzy Cognitive Maps;
 * ensures that the right knowledge base is loaded, if the fl: namespace is referred,
 * and the project contains no sub-project yet.
 * ensure_cbfcm_rules_loaded
 * @author Jean-Marc Vanel
 *
 */
public class FCMProjectListener extends AbstractProjectListener implements ProjectListener {

	private Project project;

	/**
	 * @param projectGUI
	 */
	public FCMProjectListener(ProjectGUI projectGUI) {
		project =  projectGUI.getProject();
	}

	@Override
	public void projectLoaded(Project project) {
		assert project == this.project;
		ensure_cbfcm_rules_loaded(project);
	}

	@Override
	public void n3SourceAdded(Project project, N3Source n3) {
		ensure_cbfcm_rules_loaded(project);
	}

	private void ensure_cbfcm_rules_loaded(Project project) {
		boolean fcmNeeded = false;
		final List<N3Source> list = project.getSources();
		final String flURI = Namespaces.getURIFromPrefix("fl");
		for (final N3Source n3Source : list) {
			final Set<Entry<String, URI>> entries = n3Source.getKnownURIPrefixes().entrySet();
			for (final Entry<String, URI> entry : entries) {
				final URI uri = entry.getValue();
				fcmNeeded = ( uri.toASCIIString().equals(flURI));
				if( fcmNeeded ) break;
			}
		}
		fcmNeeded = fcmNeeded && project.getSubProjects().size() == 0;
		if( fcmNeeded ) {
			try {
				project.addSubProject( ProjectFactory.restore(
						"https://deductions.svn.sourceforge.net/svnroot/deductions/fcm/cbfcm-rules.n3p" ) );
				System.out.println("FCMProjectListener.projectLoaded(): added cbfcm-rules.n3p" +
						" , in thread " + Thread.currentThread().getName() );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
}
