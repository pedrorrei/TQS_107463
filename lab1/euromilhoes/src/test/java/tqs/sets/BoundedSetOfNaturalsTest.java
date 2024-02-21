/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.sets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author ico0
 */
class BoundedSetOfNaturalsTest {
    private BoundedSetOfNaturals setA;
    private BoundedSetOfNaturals setB;
    private BoundedSetOfNaturals setC;

    @BeforeEach
    public void setUp() {
        setA = new BoundedSetOfNaturals(1);

        // o maxSize vai ser o tamanho do array dado como argumento
        setB = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30, 40, 50, 60});
        setC = BoundedSetOfNaturals.fromArray(new int[]{50, 60});
    }

    @AfterEach
    public void tearDown() {
        setA = setB = setC = null;
    }

    // @Disabled("TODO revise test logic")
    @Test
    public void testAddElement() {

        setA.add(99);
        assertTrue(setA.contains(99), "add: added element not found in set.");
        assertEquals(1, setA.size());

        // setB.add(11);
        assertTrue(setA.equals( BoundedSetOfNaturals.fromArray(new int[]{99})));
        
        assertThrows(IllegalArgumentException.class,
            () -> setB.add(11) );

        BoundedSetOfNaturals setD = new BoundedSetOfNaturals(2);
        setD.add(99);
        assertThrows(IllegalArgumentException.class,
            () -> setD.add(99) );

        assertThrows(IllegalArgumentException.class,
            () -> setD.add(-1) );


        assertTrue(!setB.contains(11), "add: added element not found in set.");
        assertEquals(6, setB.size(), "add: elements count not as expected.");
    }

    // @Disabled("TODO revise to test the construction from invalid arrays")
    @Test
    public void testAddFromBadArray() {
        int[] elems = new int[]{10, -20, -30};
        assertTrue(setA.equals( BoundedSetOfNaturals.fromArray(new int[]{})));

        // must fail with exception
        assertThrows(IllegalArgumentException.class, () -> setA.add(elems));
    }

    @Test
    public void testIntersectForNoIntersection() {
        assertFalse(setA.intersects(setB), "no intersection but was reported as existing");

    }
    


}
