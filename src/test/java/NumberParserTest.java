
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NumberParserTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private static final Map<String, String> countryCodes;

	private static final Map<String, String> nationalPrefix;
	static {
		Map<String, String> result = new HashMap<>();
		result.put("UK", "+44");
		result.put("FR", "+33");
		result.put("US", "+1");

		countryCodes = Collections.unmodifiableMap(result);
	}

	static {
		Map<String, String> result = new HashMap<>();
		result.put("UK", "0");
		result.put("FR", "0");
		result.put("US", "1");

		nationalPrefix = Collections.unmodifiableMap(result);
	}


	@Test
	public void whenCountryFromUKToUkThenUkInternational() {
		NumberParser numberParser = NumberParser.getInstance(countryCodes, nationalPrefix);
		String number = numberParser.parse("07277822334", "+447866866886");
		assertEquals("+447277822334", number);
	}

	@Test
	public void whenCountryFromUSToUSThenUSAInternational() {
		NumberParser numberParser = NumberParser.getInstance(countryCodes, nationalPrefix);
		String number = numberParser.parse("1312233244", "+1212233200");
		assertEquals("+1312233244", number);
	}

	@Test
	public void whenCountryFromUKToUSThenUSAInternational() {
		NumberParser numberParser = NumberParser.getInstance(countryCodes, nationalPrefix);
		String number = numberParser.parse("1312233244", "+447866866886");
		assertEquals("+1312233244", number);
	}

	@Test
	public void whenNumberLessThanNormalThrowException() {
		NumberParser numberParser = NumberParser.getInstance(countryCodes, nationalPrefix);
		exception.expect(RuntimeException.class);

		String number = numberParser.parse("33476767676", "+44");
	}

	@Test
	public void whenNumberFromUnknownCountryThrowException() {
		NumberParser numberParser = NumberParser.getInstance(countryCodes, nationalPrefix);
		exception.expect(RuntimeException.class);

		String number = numberParser.parse("33476767676", "+3034343434");
	}


}
