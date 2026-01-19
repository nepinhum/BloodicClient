package net.bloodic.update;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionTest
{
	@Test
	public void parseAndCompare()
	{
		Version base = Version.parse("1.2.3");
		Version newer = Version.parse("1.2.4");
		Version suffix = Version.parse("1.2.3-MC1.21.1");

		assertTrue(newer.isNewerThan(base));
		assertFalse(base.isNewerThan(newer));
		assertFalse(suffix.isNewerThan(base));
		assertFalse(base.isNewerThan(suffix));
	}
}
