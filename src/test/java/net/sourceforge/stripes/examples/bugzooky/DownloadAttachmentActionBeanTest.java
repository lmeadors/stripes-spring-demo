package net.sourceforge.stripes.examples.bugzooky;

import net.sourceforge.stripes.examples.RoundTripTestBase;
import net.sourceforge.stripes.examples.bugzooky.biz.Attachment;
import net.sourceforge.stripes.examples.bugzooky.biz.Bug;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DownloadAttachmentActionBeanTest extends RoundTripTestBase<DownloadAttachmentActionBean> {

    @Before
    public void beforeDownloadAttachmentActionBeanTest() {
        setDefaultUser();
    }

    @Test
    public void should_stream_attachment() throws Exception {

        // setup test
        trip.setParameter("attachmentIndex", "0");

        // this is odd, but we can't create a byte[] via parameters
        final Attachment attachment = new Attachment();
        attachment.setName("test.txt");
        attachment.setContentType("text/plain");
        final byte[] expected = "test content".getBytes();
        attachment.setData(expected);
        attachment.setSize(123);

        final Bug bug = new Bug();
        bug.addAttachment(attachment);

        action.setBug(bug);

        // run test
        trip.execute();

        // verify outcome
        assertEquals(0, action.getAttachmentIndex().intValue());
        final byte[] actual = trip.getOutputBytes();
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
        assertEquals(attachment.getContentType(), trip.getResponse().getContentType());
        assertEquals(123, attachment.getSize());

    }

}
