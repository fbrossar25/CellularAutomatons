package application.helpers;

import java.net.URL;

public class ResourcesHelper {
    public static final String resourcesDir = "resources/";

    public static URL getResource(String resourceName) {
        return ResourcesHelper.class.getClassLoader().getResource(resourcesDir + resourceName);
    }
}
