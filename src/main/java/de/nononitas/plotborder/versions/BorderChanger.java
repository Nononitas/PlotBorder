package de.nononitas.plotborder.versions;

import de.nononitas.plotborder.Gui;
import de.nononitas.plotborder.PlotBorder;
import org.bukkit.entity.Player;


public interface BorderChanger {
    String notYourPlot = PlotBorder.getColoredConfigString("not-your-plot");
    String notOnPlot = PlotBorder.getColoredConfigString("not-on-plot");

    void change(Gui.Type componentType, Player p, String materialToSet, String componentName);


}
