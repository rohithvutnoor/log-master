package io.github.logmaster.mask;

import io.github.logmaster.mask.implementations.EMailMaskService;
import io.github.logmaster.utils.LogObjectMapperUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EMailMaskServiceTest {

    @Mock
    private LogObjectMapperUtil logObjectMapperUtil;
    private EMailMaskService eMailMaskService;

    @Before
    public void setUp() {
        eMailMaskService = new EMailMaskService(logObjectMapperUtil);
    }

    @Test
    public void testMask() {
        // Test Case 1: Valid email with more than 5 characters in base email
        String email = "john.doe@example.com";
        String maskedEmail = eMailMaskService.mask(email);
        assertEquals("jo•••doe@example.com", maskedEmail);
        // Test Case 2: Email with 3 or 4 characters in the base part
        String email2 = "ab@domain.com";
        String maskedEmail2 = eMailMaskService.mask(email2);
        assertEquals("•b@domain.com", maskedEmail2);
        String email3 = "abcd@domain.com";
        String maskedEmail3 = eMailMaskService.mask(email3);
        assertEquals("a••d@domain.com", maskedEmail3);
        // Test Case 3: Email with 1 or 2 characters in the base part
        String email4 = "a@domain.com";
        String maskedEmail4 = eMailMaskService.mask(email4);
        assertEquals("•@domain.com", maskedEmail4);
        String email5 = "ab@domain.com";
        String maskedEmai15 = eMailMaskService.mask(email5);
        assertEquals("•b@domain.com", maskedEmai15);
        // Test Case 4: Invalid email (not containing "@")
        String email6 = "invalidemail";
        String maskedEmail6 = eMailMaskService.mask(email6);
        assertEquals(email6, maskedEmail6);
        // Test Case 5: Blank email
        String email7 = "";
        String maskedEmail7 = eMailMaskService.mask(email7);
        assertEquals(email7, maskedEmail7);
        // Test Case 6: Null email
        String email8 = null;
        String maskedEmail8 = eMailMaskService.mask(email8);
        assertEquals(email8, maskedEmail8);
    }
}

