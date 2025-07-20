package tn.bfpme.utils;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.paint.Color;
import tn.bfpme.models.User;

public class ColoredTreeRow extends TreeTableRow<User> {

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setStyle("");
        } else {
            int level = getTreeItemLevel(getTreeItem());
            Color color = getColorForLevel(level);
            setStyle("-fx-background-color: " + toHexString(color) + ";");
        }
    }

    private int getTreeItemLevel(TreeItem<User> treeItem) {
        int level = 0;
        while (treeItem != null && (treeItem = treeItem.getParent()) != null) {
            level++;
        }
        return level;
    }

    private Color getColorForLevel(int level) {
        // Cap the level to avoid extremely dark colors
        level = Math.min(level, 10);

        // Create a color gradient from light blue to darker blue
        int baseBlue = 240; // Starting point for blue
        int blue = baseBlue - (level * 20); // Decrease blue component for each level
        blue = Math.max(150, blue); // Ensure blue doesn't get too dark

        // Use consistent values for red and green to maintain blue tones
        int redGreen = 200; // Lightness of the color

        return Color.rgb(redGreen, redGreen, blue);
    }

    private String toHexString(Color color) {
        int r = (int) (255 * color.getRed());
        int g = (int) (255 * color.getGreen());
        int b = (int) (255 * color.getBlue());
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
