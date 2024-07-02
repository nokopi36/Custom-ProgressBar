package com.github.nokopi36.customprogressbar;

import javax.swing.*;
import java.util.Objects;

/**
 * 画像サイズは縦横20pxの透過背景の中に18pxの画像があるものがちょうどよかった
 * 参考画像：sample.pngのように、白い部分が透過背景、赤い部分が画像にするとちょうどよかった
 */
public interface Icons {
    String PACKAGE_PATH = "/com.github.nokopi36.customprogressbar/";

    // TODO: ProgressBarの背景画像はここで指定
    String IndeterminateBackGround = PACKAGE_PATH + "grass.png";
    String DeterminateBackGround = PACKAGE_PATH + "grass1.png";

    // TODO: 右向きの画像(右から左に流す画像)、GIF、PNGは動作確認済み
    Icon ICON1 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "sample1.png")));
    Icon ICON2 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "sample2.png")));

    // TODO: 左向きの画像(左から右に流す画像)、GIF、PNGは動作確認済み
    Icon RICON1 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "rsample1.png")));
    Icon RICON2 = new ImageIcon(Objects.requireNonNull(Icons.class.getResource(PACKAGE_PATH + "rsample2.png")));

}
