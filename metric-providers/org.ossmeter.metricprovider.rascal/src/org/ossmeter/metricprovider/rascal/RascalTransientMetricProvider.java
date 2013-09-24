package org.ossmeter.metricprovider.rascal;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.ossmeter.metricprovider.rascal.trans.model.RascalMetrics;
import org.ossmeter.platform.IMetricProvider;
import org.ossmeter.platform.ITransientMetricProvider;
import org.ossmeter.platform.MetricProviderContext;
import org.ossmeter.platform.delta.ProjectDelta;
import org.ossmeter.platform.delta.vcs.VcsProjectDelta;
import org.ossmeter.platform.delta.vcs.VcsRepositoryDelta;
import org.ossmeter.repository.model.Project;
import org.ossmeter.repository.model.VcsRepository;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.NullRascalMonitor;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.interpreter.load.StandardLibraryContributor;
import org.rascalmpl.interpreter.load.URIContributor;
import org.rascalmpl.uri.ClassResourceInputOutput;
import org.rascalmpl.uri.IURIInputStreamResolver;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.values.ValueFactoryFactory;

import com.mongodb.DB;

public class RascalTransientMetricProvider implements ITransientMetricProvider<RascalMetrics> {
	private final static GlobalEnvironment heap = new GlobalEnvironment();
	private final static ModuleEnvironment root = new ModuleEnvironment("******metrics******", heap);
	private final static IValueFactory VF = ValueFactoryFactory.getValueFactory();
	private final static OSSMeterURIResolver ossmStore = new OSSMeterURIResolver();
	private final static Evaluator eval = initEvaluator();

	private static Evaluator initEvaluator() {
		Evaluator eval = new Evaluator(VF, new PrintWriter(System.err), new PrintWriter(System.out), root, heap);
		IURIInputStreamResolver metrics = new ClassResourceInputOutput(eval.getResolverRegistry(), "metrics", RascalTransientMetricProvider.class, "");
		eval.getResolverRegistry().registerInput(metrics);
		eval.getResolverRegistry().registerInputOutput(ossmStore);
		eval.addRascalSearchPathContributor(new URIContributor(URIUtil.rootScheme("metrics")));
		eval.addRascalSearchPathContributor(StandardLibraryContributor.getInstance());
		eval.addRascalSearchPath(URIUtil.assumeCorrect("file:///Users/shahi/Documents/CWI/OSSMeter/Software/Software/source/metric-providers/org.ossmeter.metricprovider.rascal.examples/modules/"));
		return eval;
	}

	private final String function;
	private final String module;
	private final URI metric;
	protected MetricProviderContext context;

	public RascalTransientMetricProvider(URI metric) {
		this.metric = metric;
		String module = metric.getAuthority();
		this.module = module;
		eval.doImport(new NullRascalMonitor(), module);
		this.function = metric.getPath().substring(1); // drop the slash
	}

	@Override
	public boolean appliesTo(Project project) {
		return project.getVcsRepositories().size() > 0;
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public RascalMetrics adapt(DB db) {
		return new RascalMetrics(db);
	}

	@Override
	public void measure(Project project, ProjectDelta projectDelta,
			RascalMetrics db) {
		File localStorage = new File(project.getStorage().getPath());
		ossmStore.addProject(project.getName(), localStorage);
		try {
			
			System.out.println("Going into rascal code");
			VcsProjectDelta vcsDelta = projectDelta.getVcsDelta();

			for (VcsRepositoryDelta vcsRepositoryDelta : vcsDelta.getRepoDeltas()) {
				VcsRepository vcsRepository = vcsRepositoryDelta.getRepository();
				//ISourceLocation rascalLoc = VF.sourceLocation(URIUtil.create("ossm", project.getName(), "/"));
				ISourceLocation rascalLoc = VF.sourceLocation(localStorage.getAbsolutePath());
				IValue result = eval.call(new NullRascalMonitor(), module, "checkOutRepository", VF.string(vcsRepository.getUrl()), rascalLoc);
				System.out.println(result);
				result = eval.call(new NullRascalMonitor(), module, "createModel", rascalLoc);
				System.out.println(result);
				result = eval.call(new NullRascalMonitor(), module, function);
				System.out.println(result);
				/*
				 * Do stuff
				 */
				
				db.sync();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getIdentifier() {
		return metric.toASCIIString();
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {

	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Collections.emptyList();
	}

	@Override
	public String getShortIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFriendlyName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSummaryInformation() {
		// TODO Auto-generated method stub
		return null;
	}
}
