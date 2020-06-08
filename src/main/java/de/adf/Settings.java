package de.adf;

import java.util.Map;
import java.awt.*;

public abstract class Settings {
    static int SCREENWIDTH = 1000;
    static int SCREENHEIGHT = 630;

    static int PORT = 50000; // Dynamic Port Number / Private Port
    static String REMOTEOBJ = "schiffeversenken";

    static Map<String, Color> colors = Map.of(
        "background", Color.white,
        "background-disabled", Color.gray,
        "ship", Color.green,
        "hit", Color.red
    );
}