
package com.bitfire.uracer.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class NumberString {
	private static NumberFormat formatter = null;
	private static NumberFormat formatterLong = null;
	private static NumberFormat formatterVeryLong = null;

	private NumberString () {
	}

	public static String format (float value) {
		if (NumberString.formatter == null) {
			NumberString.formatter = new DecimalFormat("0.00");
		}

		return NumberString.formatter.format(AMath.round(value, 2));
// return NumberString.formatter.format( value );
	}

	public static String formatLong (float value) {
		if (NumberString.formatterLong == null) {
			NumberString.formatterLong = new DecimalFormat("0.000000");
		}

		return NumberString.formatterLong.format(AMath.round(value, 6));
// return NumberString.formatterLong.format( value );
	}

	public static String formatVeryLong (float value) {
		if (NumberString.formatterVeryLong == null) {
			NumberString.formatterVeryLong = new DecimalFormat("0.0000000000");
		}

		return NumberString.formatterVeryLong.format(value);
	}
}
