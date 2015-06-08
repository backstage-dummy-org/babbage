package com.github.onsdigital.generator.data;

import static org.junit.Assert.assertEquals;

import com.github.onsdigital.content.statistic.data.TimeSeries;
import com.github.onsdigital.content.statistic.data.timeseries.TimeseriesValue;
import org.junit.Test;


public class DataCSVTest {

	TimeSeries timeseries = new TimeSeries(null,null,null,null, null);
	TimeseriesValue timeseriesValue = new TimeseriesValue();

	@Test
	public void shouldNotScaleInteger() {

		// Given
		timeseries.setScaleFactor(1);
		timeseriesValue.value = "100";

		// When
		DataCSV.scale(timeseriesValue, timeseries);

		// Then
		assertEquals("100", timeseriesValue.value);
	}

	@Test
	public void shouldNotScaleDecimal() {

		// Given
		timeseries.setScaleFactor(1);
		timeseriesValue.value = "1.00";

		// When
		DataCSV.scale(timeseriesValue, timeseries);

		// Then
		assertEquals("1.00", timeseriesValue.value);
	}

	@Test
	public void shouldScaleInteger() {

		// Given
		timeseries.setScaleFactor(10);
		timeseriesValue.value = "100";

		// When
		DataCSV.scale(timeseriesValue, timeseries);

		// Then
		assertEquals("10.0", timeseriesValue.value);
	}

	@Test
	public void shouldScaleDecimal() {

		// Given
		timeseries.setScaleFactor(10);
		timeseriesValue.value = "10.0";

		// When
		DataCSV.scale(timeseriesValue, timeseries);

		// Then
		assertEquals("1.00", timeseriesValue.value);
	}

	@Test
	public void shouldScaleBillionInteger() {

		// Given
		timeseries.setScaleFactor(1000000);
		timeseriesValue.value = "71780230712";

		// When
		DataCSV.scale(timeseriesValue, timeseries);

		// Then
		assertEquals("71780.230712", timeseriesValue.value);
	}

	@Test
	public void shouldScaleBillionDecimal() {

		// Given
		timeseries.setScaleFactor(1000000);
		timeseriesValue.value = "71780230.712";

		// When
		DataCSV.scale(timeseriesValue, timeseries);

		// Then
		assertEquals("71.780230712", timeseriesValue.value);
	}

}
