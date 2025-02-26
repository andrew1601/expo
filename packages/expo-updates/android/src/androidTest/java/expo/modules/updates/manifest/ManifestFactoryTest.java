package expo.modules.updates.manifest;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import expo.modules.updates.UpdatesConfiguration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ManifestFactoryTest {

  private String legacyManifestJson = "{\"sdkVersion\":\"39.0.0\",\"id\":\"@esamelson/native-component-list\",\"releaseId\":\"0eef8214-4833-4089-9dff-b4138a14f196\",\"commitTime\":\"2020-11-11T00:17:54.797Z\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fnative-component-list%2F39.0.0%2F01c86fd863cfee878068eebd40f165df-39.0.0-ios.js\"}";
  private String newManifestJson = "{\"runtimeVersion\":\"1\",\"id\":\"0eef8214-4833-4089-9dff-b4138a14f196\",\"createdAt\":\"2020-11-11T00:17:54.797Z\",\"launchAsset\":{\"url\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fnative-component-list%2F39.0.0%2F01c86fd863cfee878068eebd40f165df-39.0.0-ios.js\",\"contentType\":\"application/javascript\"}}";
  private String bareManifestJson = "{\"id\":\"0eef8214-4833-4089-9dff-b4138a14f196\",\"commitTime\":1609975977832}";

  private UpdatesConfiguration createConfig() {
    HashMap<String, Object> configMap = new HashMap<>();
    configMap.put("updateUrl", Uri.parse("https://exp.host/@esamelson/native-component-list"));
    return new UpdatesConfiguration().loadValuesFromMap(configMap);
  }

  @Test
  public void testGetManifest_Legacy() throws Exception {
    ManifestResponse response = mock(ManifestResponse.class);
    when(response.header("expo-protocol-version", null)).thenReturn(null);

    Manifest actual = ManifestFactory.getManifest(
      new JSONObject(legacyManifestJson),
      response,
      createConfig()
    );
    Assert.assertTrue(actual instanceof LegacyManifest);
  }

  @Test
  public void testGetManifest_New() throws Exception {
    ManifestResponse response = mock(ManifestResponse.class);
    when(response.header("expo-protocol-version", null)).thenReturn("0");

    Manifest actual = ManifestFactory.getManifest(
      new JSONObject(newManifestJson),
      response,
      createConfig()
    );

    Assert.assertTrue(actual instanceof NewManifest);
  }

  @Test(expected = Exception.class)
  public void testGetManifest_UnsupportedProtocolVersion() throws Exception {
    ManifestResponse response = mock(ManifestResponse.class);
    when(response.header("expo-protocol-version", null)).thenReturn("1");

    ManifestFactory.getManifest(
      new JSONObject(newManifestJson),
      response,
      createConfig()
    );
  }

  @Test
  public void testGetEmbeddedManifest_Legacy() throws JSONException {
    Manifest actual = ManifestFactory.getEmbeddedManifest(
      new JSONObject(legacyManifestJson),
      createConfig()
    );
    Assert.assertTrue(actual instanceof LegacyManifest);
  }

  @Test
  public void testGetEmbeddedManifest_Legacy_Bare() throws JSONException {
    Manifest actual = ManifestFactory.getEmbeddedManifest(
      new JSONObject(bareManifestJson),
      createConfig()
    );
    Assert.assertTrue(actual instanceof BareManifest);
  }
}
