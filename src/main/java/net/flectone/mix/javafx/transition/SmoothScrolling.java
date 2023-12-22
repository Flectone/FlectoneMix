package net.flectone.mix.javafx.transition;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import lombok.NonNull;

import java.util.function.Function;

public class SmoothScrolling {

    public SmoothScrolling(@NonNull ScrollPane scrollPane) {
        customScrolling(scrollPane, scrollPane.vvalueProperty(), Bounds::getHeight);
    }

    public SmoothScrolling(@NonNull ComboBox comboBox) {
        customScrolling(comboBox,  new SimpleDoubleProperty(comboBox, "vvalue"), Bounds::getHeight);
    }

    private void customScrolling(ScrollPane scrollPane, DoubleProperty scrollDriection, Function<Bounds, Double> sizeFunc) {
        double[] frictions = new double[]{0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 6.0E-4, 5.0E-4, 3.0E-5, 1.0E-5};
        double[] pushes = new double[]{1.0};
        double[] derivatives = new double[frictions.length];
        Timeline timeline = new Timeline();
        EventHandler<MouseEvent> dragHandler = (event) -> {
            timeline.stop();
        };
        EventHandler<ScrollEvent> scrollHandler = (event) -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {
                int direction = event.getDeltaY() > 0.0 ? -1 : 1;

                for(int i = 0; i < pushes.length; ++i) {
                    derivatives[i] += (double)direction * pushes[i];
                }

                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }

                event.consume();
            }

        };

        scrollPane.getContent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
        scrollPane.getContent().addEventHandler(ScrollEvent.ANY, scrollHandler);

        if (scrollPane.getContent().getParent() != null) {
            scrollPane.getContent().getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollPane.getContent().getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }

        scrollPane.getContent().parentProperty().addListener((o, oldVal, newVal) -> {
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                oldVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }

            if (newVal != null) {
                newVal.addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                newVal.addEventHandler(ScrollEvent.ANY, scrollHandler);
            }

        });
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3.0), (event) -> {
            int i;
            for(i = 0; i < derivatives.length; ++i) {
                derivatives[i] *= frictions[i];
            }

            for(i = 1; i < derivatives.length; ++i) {
                derivatives[i] += derivatives[i - 1];
            }

            double dy = derivatives[derivatives.length - 1];
            double size = sizeFunc.apply(scrollPane.getContent().getLayoutBounds());
            scrollDriection.set(Math.min(Math.max(scrollDriection.get() + dy / size, 0.0), 1.0));
            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }

        }));
        timeline.setCycleCount(-1);
    }

    private void customScrolling(ComboBox comboBox, DoubleProperty scrollDriection, Function<Bounds, Double> sizeFunc) {
        double[] frictions = new double[]{0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 6.0E-4, 5.0E-4, 3.0E-5, 1.0E-5};
        double[] pushes = new double[]{1.0};
        double[] derivatives = new double[frictions.length];
        Timeline timeline = new Timeline();
        EventHandler<MouseEvent> dragHandler = (event) -> {
            timeline.stop();
        };
        EventHandler<ScrollEvent> scrollHandler = (event) -> {
            System.out.println(1);
            if (event.getEventType() == ScrollEvent.SCROLL) {
                int direction = event.getDeltaY() > 0.0 ? -1 : 1;

                for(int i = 0; i < pushes.length; ++i) {
                    derivatives[i] += (double)direction * pushes[i];
                }

                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }

                event.consume();
            }

        };
        comboBox.getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
        comboBox.getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3.0), (event) -> {
            int i;
            for(i = 0; i < derivatives.length; ++i) {
                derivatives[i] *= frictions[i];
            }

            for(i = 1; i < derivatives.length; ++i) {
                derivatives[i] += derivatives[i - 1];
            }

            double dy = derivatives[derivatives.length - 1];
            double size = sizeFunc.apply(comboBox.getLayoutBounds());
            scrollDriection.set(Math.min(Math.max(scrollDriection.get() + dy / size, 0.0), 1.0));
            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }

        }));
        timeline.setCycleCount(-1);
    }
}
