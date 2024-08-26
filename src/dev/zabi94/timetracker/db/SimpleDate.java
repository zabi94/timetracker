package dev.zabi94.timetracker.db;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

public record SimpleDate(int day, int month, int year) {
	
	public int getIntRepr() {
		return day+(100*month)+(10000 * year);
	}
	
	public static SimpleDate parse(int repr) {
		int year = repr / 10000;
		int month = (repr - (year*10000)) / 100;
		int day = repr - (year*10000) - (month * 100);
		return new SimpleDate(day, month, year);
	}
	
	@Override
	public String toString() {
		String repr = "" + getIntRepr();
		return repr.substring(6) + "/" + repr.substring(4, 6) + "/" + repr.substring(0, 4);
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}
	
	public SimpleDate dayBefore() {
		ZonedDateTime d = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()).minus(Period.ofDays(1));
		return new SimpleDate(d.get(ChronoField.DAY_OF_MONTH), d.get(ChronoField.MONTH_OF_YEAR), d.get(ChronoField.YEAR));
	}
	
	public SimpleDate dayAfter() {
		ZonedDateTime d = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault()).plus(Period.ofDays(1));
		return new SimpleDate(d.get(ChronoField.DAY_OF_MONTH), d.get(ChronoField.MONTH_OF_YEAR), d.get(ChronoField.YEAR));
	}

	public static SimpleDate today() {
		var d = Instant.now().atZone(ZoneId.systemDefault());
		return new SimpleDate(d.get(ChronoField.DAY_OF_MONTH), d.get(ChronoField.MONTH_OF_YEAR), d.get(ChronoField.YEAR));
	}
	
	public int dayOfWeek() {
		ZonedDateTime d = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.systemDefault());
		return d.getDayOfWeek().getValue();
	}
	
}
