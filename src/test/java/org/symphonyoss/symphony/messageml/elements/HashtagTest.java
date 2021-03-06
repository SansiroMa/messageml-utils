package org.symphonyoss.symphony.messageml.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;

import java.util.Collections;

public class HashtagTest extends ElementTest {

  @Test
  public void testHashTag() throws Exception {
    String input = "<messageML>Hello <hash tag=\"world\"/>!</messageML>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"keyword1\">#world</span>!"
        + "</div>";
    String expectedJson = "{\"keyword1\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"world\""
        + "}]}}";
    String expectedText = "world";
    String expectedMarkdown = "Hello #world!";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", Collections.emptyMap(), messageML.getChildren().get(1).getAttributes());
    verifyHashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testHashTagNonAlnum() throws Exception {
    String input = "<messageML>Hello <hash tag=\"_hello.w-o-r-l-d_\"/>!</messageML>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"keyword1\">#_hello.w-o-r-l-d_</span>!"
        + "</div>";
    String expectedJson = "{\"keyword1\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"_hello.w-o-r-l-d_\""
        + "}]}}";
    String expectedText = "_hello.w-o-r-l-d_";
    String expectedMarkdown = "Hello #_hello.w-o-r-l-d_!";

    // Verify by MessageML
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    Element messageML = context.getMessageML();
    assertEquals("Element attributes", Collections.emptyMap(), messageML.getChildren().get(1).getAttributes());
    verifyHashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);

    // Verify by PresentationML
    context.parseMessageML(expectedPresentationML, expectedJson, MessageML.MESSAGEML_VERSION);
    messageML = context.getMessageML();
    verifyHashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testHashTagByPresentationMLDiv() throws Exception {
    String input = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <div class=\"entity\" data-entity-id=\"hash123\">world</div>!"
        + "</div>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <div class=\"entity\" data-entity-id=\"hash123\">#world</div>!"
        + "</div>";
    String entityJson = "{\"hash123\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"world\""
        + "}]}}";
    String expectedText = "world";
    String expectedMarkdown = "Hello #world!";

    context.parseMessageML(input, entityJson, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", 1, messageML.getChildren().get(1).getAttributes().size());
    assertEquals("Element class attribute", "entity", messageML.getChildren().get(1).getAttribute("class"));
    verifyHashTag(messageML, expectedPresentationML, entityJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testHashTagByPresentationMLSpan() throws Exception {
    String input = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"hash123\">world</span>!"
        + "</div>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"hash123\">#world</span>!"
        + "</div>";
    String entityJson = "{\"hash123\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"world\""
        + "}]}}";
    String expectedText = "world";
    String expectedMarkdown = "Hello #world!";

    context.parseMessageML(input, entityJson, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", 1, messageML.getChildren().get(1).getAttributes().size());
    assertEquals("Element class attribute", "entity", messageML.getChildren().get(1).getAttribute("class"));
    verifyHashTag(messageML, expectedPresentationML, entityJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testHashTagByPresentationMLMissingEntityId() throws Exception {
    String input = "<messageML>Hello <span class=\"entity\">world</span>!</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("The attribute \"data-entity-id\" is required");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testHashTagInvalidCharacter() throws Exception {
    String input = "<messageML>Hello <hash tag=\"invalid chars!\"/></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Keywords may only contain alphanumeric characters, underscore, dot and dash");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testHashTagByPresentationMLInvalidCharacter() throws Exception {
    String input = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <div class=\"entity\" data-entity-id=\"hash123\">world</div>!"
        + "</div>";

    String entityJson = "{\"hash123\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"invalid chars!\""
        + "}]}}";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Keywords may only contain alphanumeric characters");
    context.parseMessageML(input, entityJson, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testHashTagInvalidAttr() throws Exception {
    String invalidAttr = "<messageML>Hello <hash tag=\"world\" class=\"label\"/>!</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"class\" is not allowed in \"hash\"");
    context.parseMessageML(invalidAttr, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testPresentationMLShorthandHashTag() throws Exception {
    String invalidElement = "<div class=\"com.symphony.presentationml\"><hash tag=\"invalid\"/></div>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Shorthand tag \"hash\" is not allowed in PresentationML");
    context.parseMessageML(invalidElement, null, MessageML.MESSAGEML_VERSION);
  }

  private void verifyHashTag(Element messageML, String expectedPresentationML, String expectedJson, String expectedText,
      String expectedMarkdown) throws Exception {
    assertEquals("Element children", 3, messageML.getChildren().size());

    Element hashtag = messageML.getChildren().get(1);

    assertEquals("Element class", HashTag.class, hashtag.getClass());
    assertEquals("Element tag name", "hash", hashtag.getMessageMLTag());
    assertEquals("Element text", expectedText, ((HashTag) hashtag).getTag());
    assertEquals("PresentationML", expectedPresentationML, context.getPresentationML());
    assertEquals("Markdown", expectedMarkdown, context.getMarkdown());
    assertEquals("EntityJSON", expectedJson, MAPPER.writeValueAsString(context.getEntityJson()));
    assertEquals("Legacy entities", 1, context.getEntities().size());

    JsonNode entity = context.getEntities().get("hashtags");
    assertNotNull("Entity node", entity);
    assertEquals("Entity count", 1, entity.size());

    assertEquals("Entity text", "#" + expectedText, entity.get(0).get("text").textValue());
    assertEquals("Entity id", "#" + expectedText, entity.get(0).get("id").textValue());
    assertEquals("Entity type", "KEYWORD", entity.get(0).get("type").textValue());
  }
}
