package tn.bfpme.utils;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.paint.Color;
import javafx.scene.control.TreeItem;

public class ColoredTreeCell<S, T> extends TreeTableCell<S, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setStyle("");
        } else {
            setText(item.toString());
            TreeTableRow<S> row = getTreeTableRow();
            if (row != null) {
                TreeItem<S> treeItem = row.getTreeItem();
                if (treeItem != null) {
                    int level = getTreeItemLevel(treeItem);
                    Color color = getColorForLevel(level);
                    setStyle("-fx-background-color: " + toHexString(color) + ";");
                }
            }
        }
    }

    private int getTreeItemLevel(TreeItem<S> treeItem) {
        int level = 0;
        while (treeItem != null && (treeItem = treeItem.getParent()) != null) {
            level++;
        }
        return level;
    }

    private Color getColorForLevel(int level) {
        level = Math.min(level, 10); // Cap the level at 10 for this example
        int baseBlue = 240; // Start with a light blue
        int blue = baseBlue - (level * 30); // Decrease blue component by 30 for each level
        blue = Math.max(blue, 150); // Ensure the color doesn't get too dark
        return Color.rgb(150, 150, blue); // Use 150 for red and green components to create shades of blue
    }

    private String toHexString(Color color) {
        int r = (int) (255 * color.getRed());
        int g = (int) (255 * color.getGreen());
        int b = (int) (255 * color.getBlue());
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
