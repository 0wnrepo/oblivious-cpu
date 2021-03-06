package com.grack.homomorphic.light;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.grack.homomorphic.light.LightBitFactory;
import com.grack.homomorphic.ops.Bit;
import com.grack.homomorphic.ops.Word;

public class LightNativeBitTest {
	private LightBitFactory factory;
	private Bit one;
	private Bit zero;

	@Before
	public void setup() {
		factory = new LightBitFactory();
		one = factory.encodeBit(1);
		zero = factory.encodeBit(0);
	}
	
	@Test
	public void bitValuesAreExtractable() {
		assertEquals(1, factory.extract(one));
		assertEquals(0, factory.extract(zero));
	}
	
	@Test
	public void wordsHaveCorrectBitValues() {
		Word word = factory.encodeWord(0b10001111, 8);
		assertEquals(1, factory.extract(word.bit(0)));
		assertEquals(1, factory.extract(word.bit(1)));
		assertEquals(1, factory.extract(word.bit(2)));
		assertEquals(1, factory.extract(word.bit(3)));
		assertEquals(0, factory.extract(word.bit(4)));
		assertEquals(0, factory.extract(word.bit(5)));
		assertEquals(0, factory.extract(word.bit(6)));
		assertEquals(1, factory.extract(word.bit(7)));
	}
	
	@Test
	public void wordsAreExtractable() {
		assertEquals(0b10001111, factory.extract(factory.encodeWord(0b10001111, 8)));
	}
	
}
