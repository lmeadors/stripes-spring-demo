package net.sourceforge.stripes.examples;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeanTestHelper {

	public <T, S extends T> void assertPropertiesAreEqual(T expected, S actual, String... excludes) throws Exception {
		PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(expected);
		for (PropertyDescriptor p : pd) {
			boolean excluded = false;
			for (String s : excludes) {
				if (s.equalsIgnoreCase(p.getName())) {
					excluded = true;
				}
			}
			if (!excluded) {
				Object expectedValue = PropertyUtils.getProperty(expected, p.getName());
				Object actualValue = PropertyUtils.getProperty(actual, p.getName());

				if ((expectedValue == null) && (actualValue == null)) {
					// We will also consider two nulls as equal, even though technically they aren't.
					// In practice, though, this method is called to test "sameness", not equality.
				} else {
					assertEquals(MessageFormat.format("Assert failed for property ''{0}''", p.getName()), expectedValue, actualValue);
				}
			}
		}
	}

	public void assertPropertiesNotNull(final Object object, String... propertyNames) throws Exception {
		for (final String property : propertyNames) {
			assertNotNull(PropertyUtils.getProperty(object, property));
		}
	}

}
