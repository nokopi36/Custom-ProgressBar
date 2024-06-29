package com.github.nokopi36.customprogressbar;

import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class ProgressBarUi extends BasicProgressBarUI {
    Icon selectedIcon;
    Icon selectedReverseIcon;
    Icon[] iconList;
    Icon[] reverseIconList;
    BufferedImage determinateBackGround = null;
    BufferedImage indeterminateBackGround = null;

    private static final float ONE_OVER_SEVEN = 1f / 7;
    private static final JBColor VIOLET = JBColor.namedColor("violet", 0x5a009d);

    public ProgressBarUi() {
        try {
            determinateBackGround = ImageIO.read(getClass().getResource(Icons.DeterminateBackGround));
            indeterminateBackGround = ImageIO.read(getClass().getResource(Icons.IndeterminateBackGround));
        } catch (IOException e) {
            e.printStackTrace();
        }
        iconList = new Icon[]{Icons.ICON1, Icons.ICON2};

        reverseIconList = new Icon[]{Icons.RICON1, Icons.RICON2};

        selectedIcon = getRandomOddIcon();
        selectedReverseIcon = getRandomEvenIcon();
    }

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
        return new ProgressBarUi();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(super.getPreferredSize(c).width, JBUI.scale(20));
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        progressBar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
            }
        });
    }

    private volatile int offset = 0;
    private volatile int offset2 = 0;
    private volatile int velocity = 1;
    boolean isReverse;

    @Override
    protected void paintIndeterminate(Graphics g2d, JComponent c) {

        if (!(g2d instanceof Graphics2D g)) {
            return;
        }

        Insets b = progressBar.getInsets(); // area for a border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }
        g.setColor(new JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50)));
        int w = c.getWidth();
        int h = c.getPreferredSize().height;
        if (isOdd(c.getHeight() - h)) h++;


        if (c.isOpaque()) {
            g.fillRect(0, (c.getHeight() - h) / 2, w, h);
        }
        g.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        g.translate(0, (c.getHeight() - h) / 2);
        Paint old = g.getPaint();

        if (indeterminateBackGround != null) {
            TexturePaint tp = new TexturePaint(indeterminateBackGround, new Rectangle2D.Double(0, 2, 16, 16));
            g.setPaint(tp);
        }

        final float R = JBUIScale.scale(8f);
        final float R2 = JBUIScale.scale(9f);
        final Area containingRoundRect = new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R));
        g.fill(containingRoundRect);
        g.setPaint(old);
        synchronized (this) {
            offset = (offset + 1) % getPeriodLength();
            offset2 += velocity;
            if (offset2 <= 2) {
                offset2 = 2;
                velocity = 1;
            } else if (offset2 >= w - JBUIScale.scale(15)) {
                offset2 = w - JBUIScale.scale(15);
                velocity = -1;
            }
        }
        Area area = new Area(new Rectangle2D.Float(0, 0, w, h));
        area.subtract(new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R)));
        g.setPaint(Gray._128);
        if (c.isOpaque()) {
            g.fill(area);
        }

        area.subtract(new Area(new RoundRectangle2D.Float(0, 0, w, h, R2, R2)));

        Container parent = c.getParent();
        Color background = parent != null ? parent.getBackground() : UIUtil.getPanelBackground();
        g.setPaint(background);
        if (c.isOpaque()) {
            g.fill(area);
        }

        Icon scaledIcon = selectedIcon;
        if (velocity > 0) {
            if (isReverse) {
                selectedIcon = getRandomOddIconExcept(scaledIcon);
            }
            scaledIcon = selectedIcon;
            isReverse = false;
        } else {
            if (!isReverse) {
                selectedReverseIcon = getRandomEvenIconExcept(scaledIcon);
            }
            scaledIcon = selectedReverseIcon;
            isReverse = true;
        }
        scaledIcon.paintIcon(progressBar, g, offset2 - JBUIScale.scale(10), -JBUIScale.scale(6)); //aaaaaaaaaaaaaaaaaaa

        g.draw(new RoundRectangle2D.Float(1f, 1f, w - 2f - 1f, h - 2f - 1f, R, R));
        g.translate(0, -(c.getHeight() - h) / 2);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width);
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height);
            }
        }
        config.restore();
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D g2)) {
            return;
        }

        if (progressBar.getOrientation() != SwingConstants.HORIZONTAL || !c.getComponentOrientation().isLeftToRight()) {
            super.paintDeterminate(g, c);
            return;
        }
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        Insets b = progressBar.getInsets(); // area for a border
        int w = progressBar.getWidth();
        int h = progressBar.getPreferredSize().height;
        if (isOdd(c.getHeight() - h)) h++;

        int barRectWidth = w - (b.right + b.left);
        int barRectHeight = h - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        Container parent = c.getParent();
        Color background = parent != null ? parent.getBackground() : UIUtil.getPanelBackground();

        g.setColor(background);
        if (c.isOpaque()) {
            g.fillRect(0, 0, w, h);
        }

        final float R = JBUI.pixScale(8f);
        final float R2 = JBUI.pixScale(9f);
        final float off = JBUI.pixScale(1f);

        g2.translate(0, (c.getHeight() - h) / 2);
        g2.setColor(progressBar.getForeground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w - off, h - off, R2, R2));
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(off, off, w - 2f * off - off, h - 2f * off - off, R, R));
        if (determinateBackGround != null) {
            TexturePaint tp = new TexturePaint(determinateBackGround, new Rectangle2D.Double(0, 2, 16, 16));
            g2.setPaint(tp);
        }

        g2.fill(new RoundRectangle2D.Float(2f * off, 2f * off, amountFull - JBUIScale.scale(5f), h - JBUIScale.scale(5f), JBUIScale.scale(7f), JBUIScale.scale(7f)));
        Icons.ICON1.paintIcon(progressBar, g2, amountFull - JBUI.scale(10), -JBUI.scale(6));
        g2.translate(0, -(c.getHeight() - h) / 2);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        }
        config.restore();
    }

    private void paintString(Graphics g, int x, int y, int w, int h, int fillStart, int amountFull) {
        if (!(g instanceof Graphics2D g2)) {
            return;
        }
        String progressString = progressBar.getString();
        g2.setFont(progressBar.getFont());
        Point renderLocation = getStringPlacement(g2, progressString, x, y, w, h);
        Rectangle oldClip = g2.getClipBounds();

        if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
            g2.setColor(getSelectionBackground());
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(fillStart, y, amountFull, h);
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
        } else { // VERTICAL
            g2.setColor(getSelectionBackground());
            AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2);
            g2.setFont(progressBar.getFont().deriveFont(rotate));
            renderLocation = getStringPlacement(g2, progressString, x, y, w, h);
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(x, fillStart, w, amountFull);
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
        }
        g2.setClip(oldClip);
    }

    @Override
    protected int getBoxLength(int availableLength, int otherDimension) {
        return availableLength;
    }

    private int getPeriodLength() {
        return JBUI.scale(16);
    }

    private static boolean isOdd(int value) {
        return value % 2 != 0;
    }

    private Icon getRandomOddIcon() {
        return getIconFromList(iconList);
    }

    private Icon getRandomOddIconExcept(Icon icon) {
        Icon toReturn = null;
        int oldIndex = ArrayUtil.indexOf(reverseIconList, icon);
        while (toReturn == null || ArrayUtil.indexOf(iconList, toReturn) == oldIndex) {
            toReturn = getIconFromList(iconList);
        }
        return toReturn;
    }

    private Icon getRandomEvenIcon() {
        return getIconFromList(reverseIconList);
    }

    private Icon getRandomEvenIconExcept(Icon icon) {
        Icon toReturn = null;
        int oldIndex = ArrayUtil.indexOf(iconList, icon);
        while (toReturn == null || ArrayUtil.indexOf(reverseIconList, toReturn) == oldIndex) {
            toReturn = getIconFromList(reverseIconList);
        }
        return toReturn;
    }

    private Icon getIconFromList(Icon[] icons) {
        Random r = new Random();
        int i = r.nextInt(icons.length);
        return icons[i];
    }
}
