public abstract class Parse extends WorldModel{
    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;

    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    private static final int MAINCHAR_NUM_PROPERTIES = 4;
    private static final int MAINCHAR_ID = 1;
    private static final int MAINCHAR_COL = 2;
    private static final int MAINCHAR_ROW = 3;

    private static final int DARKELF_NUM_PROPERTIES = 4;
    private static final int DARKELF_ID = 1;
    private static final int DARKELF_COL = 2;
    private static final int DARKELF_ROW = 3;

    private static final int PRINCESS_NUM_PROPERTIES = 4;
    private static final int PRINCESS_ID = 1;
    private static final int PRINCESS_COL = 2;
    private static final int PRINCESS_ROW = 3;

    private static final int HALO_NUM_PROPERTIES = 4;
    private static final int HALO_ID = 1;
    private static final int HALO_COL = 2;
    private static final int HALO_ROW = 3;

    private static final int REAPER_NUM_PROPERTIES = 4;
    private static final int REAPER_ID = 1;
    private static final int REAPER_COL = 2;
    private static final int REAPER_ROW = 3;

    private static final int CHEST_NUM_PROPERTIES = 4;
    private static final int CHEST_ID = 1;
    private static final int CHEST_COL = 2;
    private static final int CHEST_ROW = 3;

    public Parse(int numRows, int numCols, Background defaultBackground) {
        super(numRows, numCols, defaultBackground);
    }

    protected static boolean parseBackground(WorldModel world, String [] properties, ImageStore imageStore)
    {

        if (properties.length == BGND_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            world.setBackground(pt, new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    protected static boolean parseMainChar(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == MAINCHAR_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[MAINCHAR_COL]),
                    Integer.parseInt(properties[MAINCHAR_ROW]));
            Entity entity = MainCharacter.getMainCharacter(properties[MAINCHAR_ID],
                    pt, imageStore.getImageList(world.getMainCharKey()), 0);
            world.tryAddEntity(entity);
        }

        return properties.length == MAINCHAR_NUM_PROPERTIES;
    }

    protected static boolean parseDarkElf(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == DARKELF_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[DARKELF_COL]),
                    Integer.parseInt(properties[DARKELF_ROW]));
            Entity entity = Create.createDarkElf(properties[DARKELF_ID],
                    pt, 300, imageStore.getImageList(world.getDarkElfKey()));
            world.tryAddEntity(entity);
        }

        return properties.length == DARKELF_NUM_PROPERTIES;
    }

    protected static boolean parsePrincess(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == PRINCESS_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[PRINCESS_COL]),
                    Integer.parseInt(properties[PRINCESS_ROW]));
            Entity entity = Create.createPrincess(properties[PRINCESS_ID],
                    pt, 1200, imageStore.getImageList(world.getPrincessKey()));
            world.tryAddEntity(entity);
        }

        return properties.length == PRINCESS_NUM_PROPERTIES;
    }

    protected static boolean parseHalo(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == HALO_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[HALO_COL]),
                    Integer.parseInt(properties[HALO_ROW]));
            Entity entity = Create.createHalo(properties[HALO_ID],
                    pt, 1000, 500, imageStore.getImageList(world.getHaloKey()));
            world.tryAddEntity(entity);
        }

        return properties.length == HALO_NUM_PROPERTIES;
    }

    protected static boolean parseReaper(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == REAPER_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[REAPER_COL]),
                    Integer.parseInt(properties[REAPER_ROW]));
            Entity entity = Create.createReaper(properties[REAPER_ID],
                    pt, 500, imageStore.getImageList(world.getReaperKey()));
            world.tryAddEntity(entity);
        }

        return properties.length == DARKELF_NUM_PROPERTIES;
    }

    protected static boolean parseChest(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == CHEST_NUM_PROPERTIES)
        {
            Point pt = new Point(Integer.parseInt(properties[CHEST_COL]),
                    Integer.parseInt(properties[CHEST_ROW]));
            Entity entity = Create.createChest(properties[CHEST_ID],
                    pt, 100, imageStore.getImageList(world.getChestKey()));
            world.tryAddEntity(entity);
        }

        return properties.length == CHEST_NUM_PROPERTIES;
    }

    protected static boolean parseObstacle(WorldModel world, String [] properties, ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES)
        {
            Point pt = new Point(
                    Integer.parseInt(properties[OBSTACLE_COL]),
                    Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = Create.createObstacle(properties[OBSTACLE_ID],
                    pt, imageStore.getImageList(world.getObstacleKey()));
            world.tryAddEntity(entity);
        }

        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }
}
