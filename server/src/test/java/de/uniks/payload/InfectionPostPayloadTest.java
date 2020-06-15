package de.uniks.payload;

import org.junit.Test;

import static org.junit.Assert.*;

public class InfectionPostPayloadTest {

    @Test
    public void isValid() {
        InfectionPostPayload ipp = new InfectionPostPayload();
        assertFalse(ipp.isValid());

        ipp.setTime(100);
        ipp.setId(1);
        assertFalse(ipp.isValid());

        ipp.setTime(1590969600);
        ipp.setId(1);
        assertTrue(ipp.isValid());
    }

    @Test
    public void getTime() {
        InfectionPostPayload ipp = new InfectionPostPayload();
        assertEquals(0, ipp.getTime());
    }

    @Test
    public void getId() {
        InfectionPostPayload ipp = new InfectionPostPayload();
        assertEquals(0, ipp.getId());
    }

    @Test
    public void setTime() {
        InfectionPostPayload ipp = new InfectionPostPayload();
        ipp.setTime(100);
        assertEquals(100, ipp.getTime());
    }

    @Test
    public void setId() {
        InfectionPostPayload ipp = new InfectionPostPayload();
        ipp.setId(100);
        assertEquals(100, ipp.getId());
    }
}
