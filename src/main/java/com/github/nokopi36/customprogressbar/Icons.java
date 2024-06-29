package com.github.nokopi36.customprogressbar;

import javax.swing.*;
import java.util.Objects;

public interface Icons {
    String PACKAGE_PATH = "/com.github.nokopi36.customprogressbar/";

    String IndeterminateBackGround = PACKAGE_PATH + "grass.png";
    String DeterminateBackGround = PACKAGE_PATH + "grass.png";

    Icon ICON1 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "0.gif")));
    Icon ICON2 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "rsz_cat.png")));

    Icon RICON1 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "tbsten2.png")));
    Icon RICON2 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "rsz_rcat.png")));
}
