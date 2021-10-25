
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NumberParserTest {

	private static final String VALID_INTERNATIONAL_UK_NUMBER = "+447866866886";
	private static final String VALID_NATIONAL_UK_NUMBER = "07277822334";
	private static final String VALID_INTERNATIONAL_US_NUMBER = "+1212233200";
	private static final String VALID_INTERNATIONAL_DIALLED_US_NUMBER = "+1312233244";
	private static final String VALID_INTERNATIONAL_DIALLED_UK_NUMBER = "+447277822334";
	private static final String VALID_INTERNATIONAL_US_NUMBER_WITHOUT_PLUS = "1312233244";

	private static final String INVALID_USER_NUMBER = "+44";
	private static final String UNKNOWN_USER_NUMBER = "+3034343434";
	private static final String UNKNOWN_DIALLED_NUMBER = "9312233244"; //no national or international

	private static NumberParser numberParser = null;

	@BeforeClass
	public static void init() {
		numberParser = NumberParser.getInstance(countryCodes, nationalPrefix);
	}

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
		String number = numberParser.parse(VALID_NATIONAL_UK_NUMBER, VALID_INTERNATIONAL_UK_NUMBER);
		assertEquals(VALID_INTERNATIONAL_DIALLED_UK_NUMBER, number);
	}

	@Test
	public void whenCountryFromUSToUSThenUSAInternational() {
		String number = numberParser.parse(VALID_INTERNATIONAL_US_NUMBER_WITHOUT_PLUS, VALID_INTERNATIONAL_US_NUMBER);
		assertEquals(VALID_INTERNATIONAL_DIALLED_US_NUMBER, number);
	}

	@Test
	public void whenCountryFromUKToUSThenUSAInternational() {
		String number = numberParser.parse(VALID_INTERNATIONAL_US_NUMBER_WITHOUT_PLUS, VALID_INTERNATIONAL_UK_NUMBER);
		assertEquals(VALID_INTERNATIONAL_DIALLED_US_NUMBER, number);
	}

	@Test
	public void whenNumberLessThanNormalThrowException() {
		exception.expect(RuntimeException.class);

		numberParser.parse(VALID_INTERNATIONAL_US_NUMBER_WITHOUT_PLUS, INVALID_USER_NUMBER);
	}

	@Test
	public void whenNumberFromUnknownCountryThrowException() {
		exception.expect(RuntimeException.class);

		numberParser.parse(VALID_INTERNATIONAL_US_NUMBER_WITHOUT_PLUS, UNKNOWN_USER_NUMBER);
	}

	@Test
	public void whenNumberToUnknownNumberThrowException() {
		exception.expect(RuntimeException.class);

		numberParser.parse(UNKNOWN_DIALLED_NUMBER, VALID_INTERNATIONAL_UK_NUMBER);
	}

}
