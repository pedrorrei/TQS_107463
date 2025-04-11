/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.sets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.sets.BoundedSetOfNaturals;

/**
 * @author ico0
 */
class BoundedSetOfNaturalsTest {
    private BoundedSetOfNaturals setA;
    private BoundedSetOfNaturals setB;
    private BoundedSetOfNaturals setC;
    private BoundedSetOfNaturals setD;
    private BoundedSetOfNaturals setE;  


    @BeforeEach
    public void setUp() {
        setA = new BoundedSetOfNaturals(1);
        setB = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30, 40, 50, 60});
        setC = BoundedSetOfNaturals.fromArray(new int[]{50, 60});
        setD = BoundedSetOfNaturals.fromArray(new int[]{40, 50});
        setE = BoundedSetOfNaturals.fromArray(new int[]{30, 60});
    }

    @AfterEach
    public void tearDown() {
        setA = setB = setC = setD = setE = null;
    }

    @DisplayName("Test of add method, of class BoundedSetOfNaturals.")
    @Test
    public void testAddElement() {

        setA.add(99);
        assertTrue(setA.contains(99), "add: added element not found in set.");
        assertEquals(1, setA.size());

        assertThrows(IllegalArgumentException.class, () -> setB.add(12), "add: adding to full set did not throw exception.");
        assertThrows(IllegalArgumentException.class, () -> setC.add(60), "add: adding existing element did not throw exception.");
    }

    @DisplayName("Test of add method from bad array")    
    @Test
    public void testAddFromBadArray() {
        int[] elems = new int[]{10, -20, -30};

        assertThrows(IllegalArgumentException.class, () -> setA.add(elems));
    }

    @DisplayName("Test of contains method")
    @Test
    public void testContains() {
        assertTrue(setB.contains(30));
        assertFalse(setC.contains(20));
    }

    @DisplayName("Test of Intersect method")
    @Test
    public void testIntersects() {
        assertTrue(setC.intersects(setB));
        assertFalse(setE.intersects(setD));
        assertTrue(setE.intersects(setB));
    }

    @DisplayName("Test of predefined size")
    @Test
    public void testPredefinedSize() {
        assertEquals(6, setB.size());
        assertEquals(2, setC.size());
    }
}
