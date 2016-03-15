/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * (C) Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package org.opensirf.jaxrs.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.opensirf.jaxrs.api.StorageContainerStrategy;
import org.opensirf.jaxrs.api.StrategyFactory;
import org.opensirf.jaxrs.config.SIRFConfiguration;
import org.opensirf.jaxrs.config.SIRFConfigurationUnmarshaller;

public class SwiftStrategyTest {

	@Test
	public void testPushPo() throws IOException {
		String s = new String(Files.readAllBytes(Paths.get(SIRFConfiguration.SIRF_DEFAULT_DIRECTORY + "conf.json")));
		SIRFConfiguration config = new SIRFConfigurationUnmarshaller().unmarshalConfig(s);
		StorageContainerStrategy strat = StrategyFactory.createStrategy(config);
		byte[] b = "Hello 123".getBytes();
		strat.pushPreservationObject("aaaa", b);
	}
}
