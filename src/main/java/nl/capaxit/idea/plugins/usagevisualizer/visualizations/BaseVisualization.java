package nl.capaxit.idea.plugins.usagevisualizer.visualizations;

import nl.capaxit.idea.plugins.usagevisualizer.configuration.UsageVisualizationConfig;

import java.awt.*;

/**
 * Created by jamiecraane on 12/04/2017.
 */
public abstract class BaseVisualization implements UsageVisualization {
    private static final char[] IDENTIFIERS = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    protected final UsageVisualizationConfig config;
    protected final Color lineColor;

    protected BaseVisualization(final UsageVisualizationConfig config) {
        this.config = config;
        final Color color = Color.decode("#" + config.getLineColor());
        this.lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
    }

    protected final void drawArrowTip(final Graphics2D graphics, final int startX, final int startY, final int endX, final int endY) {
        final int dx = endX - startX, dy = endY - startY;
        final double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - 10, xn = xm, ym = 5, yn = -5, x;
        final double sin = dy / D;
        final double cos = dx / D;

        x = xm * cos - ym * sin + startX;
        ym = xm * sin + ym * cos + startY;
        xm = x;

        x = xn * cos - yn * sin + startX;
        yn = xn * sin + yn * cos + startY;
        xn = x;

        final int[] xpoints = {endX, (int) xm, (int) xn};
        final int[] ypoints = {endY, (int) ym, (int) yn};
        graphics.fillPolygon(xpoints, ypoints, 3);
    }

    public static final String getIdentifier(final int index) {
        return String.valueOf(IDENTIFIERS[Math.min(index, IDENTIFIERS.length - 1)]);
    }

    protected final Color getCircleColor() {
        return new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue());
    }
}
