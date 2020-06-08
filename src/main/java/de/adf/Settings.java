package de.adf;

import java.util.Map;
import java.awt.*;

/**
 * Enthält Einstellungen für das Programm
 */
public abstract class Settings {
    /** Breite des Fensters */
    static int SCREENWIDTH = 1000;
    /** Höhe des Fensters */
    static int SCREENHEIGHT = 630;

    /** Port für RMI Verbindung */
    static int PORT = 50000; // Dynamic Port Number / Private Port
    /** Registry Name */
    static String REMOTEOBJ = "schiffeversenken";

    /** Alle Farben welche im Gameboard verwendet werden */
    static Map<String, Color> colors = Map.of(
        "background", Color.white,
        "background-disabled", Color.gray,
        "ship", Color.green,
        "hit", Color.red
    );
}