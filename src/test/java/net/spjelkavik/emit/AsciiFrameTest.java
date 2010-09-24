package net.spjelkavik.emit;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * User: hennings
 * Date: 24.sep.2010
 */
public class AsciiFrameTest {

    AsciiFrame af;

    @Before
    public void setUpFrame() {
         af = new AsciiFrame();
        af.setBytes(new int[]
                {
                        2,78,52,56,48,49,48,55,9,67,49,55,53,9,3,13,10
                });
    }

    @Test
    public void testGetBadgeNo() throws Exception {
        assertEquals(480107,af.getBadgeNo());
    }

    @Test
    public void testGetStation() throws Exception {
        assertEquals(175,af.getStation());
    }
}
