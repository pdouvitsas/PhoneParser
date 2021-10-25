
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class NumberParser {

	private static final String PLUS = "+"; 
	private static final int MIN_COUNTY_LENGTH = 2;  //USA + and the number 1 
	private static final int NATIONAL_PREFIX = 1;


	private Map<String, String> countryCodes;
	private Map<String, String> nationalTrunkPrefixes;
	private int maxLengthCountryPrefix;

	private static NumberParser instance = null;


	private NumberParser(Map<String, String> countryCodes, Map<String, String> nationalTrunkPrefixes) {
		this.countryCodes = countryCodes;
		this.nationalTrunkPrefixes = nationalTrunkPrefixes;
		this.maxLengthCountryPrefix = findMaxValueOfCountryPrefix();

	}

	public static synchronized NumberParser getInstance(Map<String, String> countryCodes, Map<String, String> nationalTrunkPrefixes) {
		if (instance == null) {
			instance = new NumberParser(countryCodes, nationalTrunkPrefixes);
		}
		return instance;
	}


	public String parse(String dialledNumber, String userNumber) {
		Entry<String, String> userCountry = findUserCountry(userNumber);

		if (isNationalNumber(userCountry.getKey(), dialledNumber)) {
			return userCountry.getValue() + getNumberWithoutNationalPrefix(dialledNumber);
		} else {
			//check that the dialled number if not national is a valid international number
			String internationalDialledNunber = PLUS + dialledNumber;
			findUserCountry(internationalDialledNunber);
			return internationalDialledNunber;
		}

	}

	private int findMaxValueOfCountryPrefix() {

		int maxLength  = countryCodes.entrySet().stream()
				.map(e -> e.getValue())
				.mapToInt(String::length)
				.max().orElseThrow(() -> new RuntimeException("The list of country map is empty"));

		return maxLength;

	}

	private Entry<String, String> findUserCountry (String userNumber) {
		if (userNumber == null || userNumber.length() <= maxLengthCountryPrefix) {
			throw new IllegalArgumentException("User Number cannot be less that the international prefix");
		}

		for (int i=MIN_COUNTY_LENGTH; i<=maxLengthCountryPrefix ; i++) {
			String countryPrefix = userNumber.substring(0, i);

			Optional<Entry<String, String>> optCountryEntry = getEntryByValue(countryPrefix);
			if (optCountryEntry.isPresent()){
				return optCountryEntry.get();
			} 
		}

		throw new RuntimeException("Country was not found for the user number");

	}

	private Optional<Entry<String, String>> getEntryByValue(String value) {
		return this.countryCodes.entrySet()
				.stream()
				.filter(entry -> entry.getValue().equals(value))
				.findFirst();
	}


	private String getNationalPrefix(String dialledNumber) {
		return dialledNumber.substring(0,NATIONAL_PREFIX);
	}

	private String getNumberWithoutNationalPrefix(String dialledNumber) {
		return dialledNumber.substring(NATIONAL_PREFIX);
	}

	private boolean isNationalNumber(String countryCode, String dialledNumber) {
		String nationalPrefix = nationalTrunkPrefixes.get(countryCode);

		if (nationalPrefix == null) {
			throw new RuntimeException("No national prefix found"); 
		}

		String nationalPrefixOfDialledNumber = getNationalPrefix(dialledNumber);

		if (nationalPrefixOfDialledNumber.equals(nationalPrefix)) {
			return true;
		}


		return false;

	}


}
