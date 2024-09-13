package dev.zabi94.timetracker.gui.components;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;

import dev.zabi94.timetracker.gui.AppStyle;

public class SelectableListElementController<T extends Component> implements MouseListener {
	
	private boolean hovered = false, selected = false, zebra = false;
	private long lastClickTime = 0;
	private final T component;
	private final Runnable onClick, onDoubleClick;
	private final SelectableListController<T> controller;
	
	private SelectableListElementController(T component, Runnable onClick, Runnable onDoubleClick, SelectableListController<T> controller) {
		this.component = component;
		this.onClick = onClick;
		this.onDoubleClick = onDoubleClick;
		this.controller = controller;
		component.addMouseListener(this);
	}
	
	public boolean isHovered() {
		return hovered;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public boolean isZebra() {
		return zebra;
	}
	
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
		updateBackground();
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateBackground();
	}
	
	public void setZebra(boolean zebra) {
		this.zebra = zebra;
		updateBackground();
	}
	
	public void updateBackground() {
		if (isSelected()) {
			component.setBackground(AppStyle.BG_SELECTED);
		} else if (isHovered()) {
			component.setBackground(AppStyle.BG_HOVER);
		} else if (isZebra()) {
			component.setBackground(AppStyle.BG_ZEBRA_1);
		} else {
			component.setBackground(AppStyle.BG_ZEBRA_2);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setHovered(true);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setHovered(false);;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			long now = System.currentTimeMillis();
			if (now - lastClickTime < 400) {
				onDoubleClick.run();
			}
			lastClickTime = now;
		}
		onClick.run();
		controller.onElementClicked(this);
		setSelected(true);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// NO OP
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// NO OP
	}
	
	public T getComponent() {
		return component;
	}
	
	public static class SelectableListController<T extends Component> {
		
		private final List<SelectableListElementController<T>> elements = new ArrayList<>();
		private Consumer<SelectableListElementController<T>> onRemove;
		private boolean zebra = false;
		private T selectedElement;
		
		public SelectableListController(Consumer<SelectableListElementController<T>> onRemove) {
			setOnRemoveAction(onRemove);
		}
		
		public SelectableListController() {
			this(t -> {});
		}
		
		public void setOnRemoveAction(Consumer<SelectableListElementController<T>> onRemove) {
			this.onRemove = onRemove;
		}
		
		public void onElementClicked(SelectableListElementController<T> element) {
			elements.forEach(el -> {
				if (el != element) {
					el.setSelected(false);
					el.updateBackground();
					selectedElement = element.getComponent();
				}
			});
		}
		
		public SelectableListElementController<T> enroll(T component, Runnable onClick, Runnable onDoubleClick) {
			SelectableListElementController<T> res = new SelectableListElementController<T>(component, onClick, onDoubleClick, this);
			elements.add(res);
			res.setZebra(zebra);
			zebra = !zebra;
			return res;
		}
		
		public T getSelectedElement() {
			return selectedElement;
		}
		
		public void reloadZebra() {
			zebra = false;
			elements.forEach(e -> {
				e.setZebra(zebra);
				zebra = !zebra;
			});
		}
		
		public void remove(SelectableListElementController<T> element) {
			elements.remove(element);
			onRemove.accept(element);
			reloadZebra();
		}
		

		public void removeIf(Predicate<SelectableListElementController<T>> componentTest) {
			elements.removeIf(t -> componentTest.test(getElementFromComponent(selectedElement)));
		}
		
		private SelectableListElementController<T> getElementFromComponent(Component c) {
			return elements.stream().filter(el -> (el.getComponent() == c)).findAny().orElseThrow(NoSuchElementException::new);
		}
		
	}
	
}
