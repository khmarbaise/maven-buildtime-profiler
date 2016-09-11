package com.soebes.maven.extensions;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Compares two Maven life cycle phases (given as Strings) for order, which in
 * this case is the order in that the phases are executed by Maven (see
 * <code>http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html</code>).
 * In addition, we assume following order of built-in life cycles:
 * <code>clean</code>, <code>default</code>, <code>site</code>. Custom life
 * cycles or phases are considered to be of highest order.
 * 
 * @author Christoph Amshoff
 */
public class LifeCyclePhaseComparator implements Comparator<String> {

	private static Map<String, Integer> phaseOrderMap = new HashMap<>();

	static {
		// Clean Lifecycle
		addPhasesInOrder(1, "pre-clean", "clean", "post-clean");

		// Default Lifecycle
		addPhasesInOrder(10, "validate", "initialize", "generate-sources", "process-sources", "generate-resources",
				"process-resources", "compile", "process-classes", "generate-test-sources", "process-test-sources",
				"generate-test-resources", "process-test-resources", "test-compile", "process-test-classes", "test",
				"prepare-package", "package", "pre-integration-test", "integration-test", "post-integration-test",
				"verify", "install", "deploy");

		// Site Lifecycle
		addPhasesInOrder(50, "pre-site", "site", "post-site", "site-deploy");
	}

	@Override
	public int compare(String phase1, String phase2) {
		int order1 = phaseOrderMap.containsKey(phase1) ? phaseOrderMap.get(phase1) : 99;
		int order2 = phaseOrderMap.containsKey(phase2) ? phaseOrderMap.get(phase2) : 99;
		return Integer.compare(order1, order2);
	}

	private static void addPhasesInOrder(int startOrdinalNumber, String... phases) {
		int ordinalNumber = startOrdinalNumber;
		for (String phase : phases) {
			phaseOrderMap.put(phase, ordinalNumber++);
		}
	}
}
