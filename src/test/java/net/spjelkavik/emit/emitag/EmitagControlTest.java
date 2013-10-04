package net.spjelkavik.emit.emitag;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmitagControlTest {

    @Test
    public void testCreation() {
        String str = "5-250-67992802099-00:54:33.068-09:43:13.352-0";
        EmitagControl c = new EmitagControl(str);
        assertEquals("Entry number", 5, c.getNr());
        assertEquals("Code", 250, c.getCode());
        assertEquals("millistamp", 67992802099L, c.getMillistamp());
        assertEquals("timestamp", "00:54:33.068", c.getTimestamp());
        assertEquals("tod", "09:43:13.352", c.getTimeofday());
        assertEquals("last???", 0, c.getLast());
    }
}
