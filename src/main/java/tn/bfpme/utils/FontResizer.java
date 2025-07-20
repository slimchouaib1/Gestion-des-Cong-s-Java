package tn.bfpme.utils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

public class FontResizer {

    public static void resizeFonts(Node node, double width, double height) {
        double fontSize = Math.min(width, height) / 50;
        double fontSize2 = Math.min(width, height) / 50;
        double fontSize3 = Math.min(width, height) / 50;
        fontSize = Math.max(16, Math.min(fontSize, 28));
        fontSize2 = Math.max(13, Math.min(fontSize2, 16));
        fontSize3 = Math.max(12, Math.min(fontSize3, 14));

        if (node instanceof Label) {
            ((Label) node).setStyle("-fx-font-size: " + fontSize + "px;");
        } else if (node instanceof TextField) {
            ((TextField) node).setStyle("-fx-font-size: " + fontSize2 + "px;");
        } else if (node instanceof TextArea) {
            ((TextArea) node).setStyle("-fx-font-size: " + fontSize2 + "px;");
        } else if (node instanceof RadioButton) {
            ((RadioButton) node).setStyle("-fx-font-size: " + fontSize2 + "px;");
        } else if (node instanceof TextInputControl) {
            ((TextInputControl) node).setStyle("-fx-font-size: " + fontSize3 + "px;");
        } else if (node instanceof ComboBox) {
            ((ComboBox<?>) node).setStyle("-fx-font-size: " + fontSize3 + "px;");
        } else if (node instanceof Button) {
            ((Button) node).setStyle("-fx-font-size: " + fontSize2 + "px;");
        } else if (node instanceof DatePicker) {
            ((DatePicker) node).setStyle("-fx-font: " + fontSize2 + "px \"Roboto Regular\";");
        } else if (node instanceof TabPane) {
            TabPane tabPane = (TabPane) node;
            for (Tab tab : tabPane.getTabs()) {
                Node tabLabel = findTabLabel(tab);
                if (tabLabel != null) {
                    tabLabel.setStyle("-fx-font-size: " + fontSize2 + "px;");
                }
                resizeFonts(tab.getContent(), width, height);
            }
        } else if (node instanceof ListView) {
            ((ListView<?>) node).setStyle("-fx-font-size: " + fontSize3 + "px;");
        } else if (node instanceof TreeView) {
            TreeView<?> treeView = (TreeView<?>) node;
            treeView.setStyle("-fx-font-size: " + fontSize3 + "px;");
            resizeTreeItems(treeView.getRoot(), fontSize2);
        } else if (node instanceof TableView) {
            TableView<?> tableView = (TableView<?>) node;
            tableView.setStyle("-fx-font-size: " + fontSize2 + "px;");
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            for (TableColumn<?, ?> column : tableView.getColumns()) {
                resizeTableColumn(column, fontSize2);
            }

            tableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                double availableWidth = newWidth.doubleValue();
                int numColumns = tableView.getColumns().size();

                for (TableColumn<?, ?> column : tableView.getColumns()) {
                    column.setPrefWidth(availableWidth / numColumns);
                }
            });
        } else if (node instanceof TreeTableView) {
            TreeTableView<?> treeTableView = (TreeTableView<?>) node;
            treeTableView.setStyle("-fx-font-size: " + fontSize2 + "px;");
            treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

            for (TreeTableColumn<?, ?> column : treeTableView.getColumns()) {
                resizeTreeTableColumn(column, fontSize2);
            }

            treeTableView.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                double availableWidth = newWidth.doubleValue();
                int numColumns = treeTableView.getColumns().size();

                for (TreeTableColumn<?, ?> column : treeTableView.getColumns()) {
                    column.setPrefWidth(availableWidth / numColumns);
                }
            });
        }

        // Recursively apply font resizing to child nodes
        if (node instanceof Pane) {
            for (Node child : ((Pane) node).getChildren()) {
                resizeFonts(child, width, height);
            }
        } else if (node instanceof AnchorPane) {
            for (Node child : ((AnchorPane) node).getChildren()) {
                resizeFonts(child, width, height);
            }
        } else if (node instanceof HBox) {
            for (Node child : ((HBox) node).getChildren()) {
                resizeFonts(child, width, height);
            }
        } else if (node instanceof VBox) {
            for (Node child : ((VBox) node).getChildren()) {
                resizeFonts(child, width, height);
            }
        } else if (node instanceof ScrollPane) {
            resizeFonts(((ScrollPane) node).getContent(), width, height);
        } else if (node instanceof GridPane) {
            for (Node child : ((GridPane) node).getChildren()) {
                resizeFonts(child, width, height);
            }
        } else if (node instanceof TabPane) {
            TabPane tabPane = (TabPane) node;
            for (Tab tab : tabPane.getTabs()) {
                resizeFonts(tab.getContent(), width, height);
            }
        }
    }

    private static void resizeTreeItems(TreeItem<?> treeItem, double fontSize) {
        if (treeItem != null) {
            Node graphic = treeItem.getGraphic();
            if (graphic instanceof Labeled) {
                ((Labeled) graphic).setStyle("-fx-font-size: " + fontSize + "px;");
            }
            for (TreeItem<?> child : treeItem.getChildren()) {
                resizeTreeItems(child, fontSize);
            }
        }
    }


    private static <S, T> void resizeTableColumn(TableColumn<S, T> column, double fontSize) {
        // Set the style for the column header
        Node columnHeader = column.getGraphic();
        if (columnHeader instanceof Labeled) {
            ((Labeled) columnHeader).setStyle("-fx-font-size: " + fontSize + "px;");
        }

        // Set the style for each cell in the column
        column.setCellFactory(col -> {
            TableCell<S, T> cell = new TableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.toString());
                    } else {
                        setText(null);
                    }
                }
            };
            cell.setStyle("-fx-font-size: " + fontSize + "px;");
            return cell;
        });
    }

    private static <S, T> void resizeTreeTableColumn(TreeTableColumn<S, T> column, double fontSize) {
        Node columnHeader = column.getGraphic();
        if (columnHeader instanceof Labeled) {
            ((Labeled) columnHeader).setStyle("-fx-font-size: " + fontSize + "px;");
        }
        column.setCellFactory(col -> {
            TreeTableCell<S, T> cell = new TreeTableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.toString());
                        setStyle("-fx-font-size: " + fontSize + "px;");
                        double textWidth = getTextWidth(item.toString(), fontSize);
                        double currentWidth = column.getWidth();
                        if (textWidth > currentWidth) {
                            column.setPrefWidth(textWidth + 20);
                        }
                    } else {
                        setText(null);
                    }
                }
            };
            return cell;
        });
    }

    private static double getTextWidth(String text, double fontSize) {
        // Use a temporary Text node to calculate the width of the text
        Text tempText = new Text(text);
        tempText.setFont(Font.font(fontSize));
        return tempText.getLayoutBounds().getWidth();
    }

    private static Node findTabLabel(Tab tab) {
        if (tab.getGraphic() instanceof Labeled) {
            return tab.getGraphic();
        }
        if (tab.getText() != null && !tab.getText().isEmpty()) {
            Label label = new Label(tab.getText());
            tab.setGraphic(label);
            tab.setText(null);
            return label;
        }
        return null;
    }

    public static Stage getStageFromNode(Node node) {
        Scene scene = node.getScene();
        if (scene != null) {
            Window window = scene.getWindow();
            if (window instanceof Stage) {
                return (Stage) window;
            }
        }
        return null;
    }
}
