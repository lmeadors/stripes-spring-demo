package net.sourceforge.stripes.examples.bugzooky.bean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttachmentTest {

    @Test
    public void should_provide_preview_for_binary_types() {

        // setup test
        final Attachment attachment = new Attachment();
        attachment.setData("this is a test of the preview and needs to be long".getBytes());
        attachment.setContentType("not/text"); // almost anything here is fine

        // run test
        final String preview = attachment.getPreview();

        // verify outcome
        assertEquals("[Binary File]", preview);

    }

    @Test
    public void should_provide_preview_for_text_types() {

        // setup test
        final Attachment attachment = new Attachment();
        attachment.setData("this is a test of the preview and needs to be long".getBytes());
        attachment.setContentType("text/plain");

        // run test
        final String preview = attachment.getPreview();

        // verify outcome
        assertEquals("this is a test of the preview ", preview);

    }

    @Test
    public void should_track_size() {
        // setup test
        final Attachment attachment = new Attachment();
        attachment.setSize(123);

        // run test

        // verify outcome
        assertEquals(123, attachment.getSize());

    }

}
