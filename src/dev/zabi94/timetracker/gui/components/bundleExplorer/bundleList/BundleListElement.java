package dev.zabi94.timetracker.gui.components.bundleExplorer.bundleList;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dev.zabi94.timetracker.entity.ActivityBundle;

public class BundleListElement extends JPanel {

	private static final long serialVersionUID = 1844434694630728807L;
	private static final Dimension MARKER_DIM = new Dimension(16, 16);

	private final ActivityBundle bundle;
	
	public BundleListElement(ActivityBundle bundle) {
		this.bundle = bundle;
		this.setLayout(new GridBagLayout());
		
		JLabel customer = new JLabel(bundle.getCustomer());
		JLabel description = new JLabel(bundle.getDescription());
		
		JPanel marker = new JPanel();
		marker.setMaximumSize(MARKER_DIM);
		marker.setMinimumSize(MARKER_DIM);
		marker.setPreferredSize(MARKER_DIM);
		marker.setSize(MARKER_DIM);
		marker.setBackground(bundle.getType().color);
		marker.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, getForeground()));
	}

}
