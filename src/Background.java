import processing.core.PImage;

import java.util.List;

final class Background {
    private final String id;
    private final List<PImage> images;
    private int imageIndex;

    public Background(String id, List<PImage> images)
    {
        this.id = id;
        this.images = images;
    }

    public PImage getCurrentImage()
    {
        return (images.get(imageIndex));
    }
    public void setImageIndex(int i) { imageIndex = i; }
}
