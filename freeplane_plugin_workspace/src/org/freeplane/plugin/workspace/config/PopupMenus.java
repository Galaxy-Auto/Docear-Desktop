package org.freeplane.plugin.workspace.config;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.ControllerPopupMenuListener;
import org.freeplane.core.ui.MenuBuilder;
import org.freeplane.features.mode.Controller;
import org.freeplane.plugin.workspace.config.actions.CollapseWorkspaceTree;
import org.freeplane.plugin.workspace.config.actions.CopyPhysicalNode;
import org.freeplane.plugin.workspace.config.actions.CutPhysicalNode;
import org.freeplane.plugin.workspace.config.actions.DeletePhysicalNode;
import org.freeplane.plugin.workspace.config.actions.ExpandWorkspaceTree;
import org.freeplane.plugin.workspace.config.actions.HideWorkspace;
import org.freeplane.plugin.workspace.config.actions.PastePhysicalNode;
import org.freeplane.plugin.workspace.config.actions.RenamePhysicalNode;
import org.freeplane.plugin.workspace.config.actions.SetWorkspaceLocation;

public class PopupMenus {

	private static final String WORKSPACE_POPUP_MENU_KEY = "/workspace_popup";
	private static final String WORKSPACE_NODE_POPUP_MENU_KEY = "/workspace_node_popup";
	private static final String WORKSPACE_POPUP_MENU_CONFIG = "/xml/popup_menus.xml";
	private static final String WORKSPACE_PHYSICAL_NODE_POPUP_MENU_KEY = "workspace_physical_node_popup";

	private final HashMap<String, PopupObject> popupMap;

	public PopupMenus() {
		Controller.getCurrentModeController().addAction(new ExpandWorkspaceTree());
		Controller.getCurrentModeController().addAction(new CollapseWorkspaceTree());
		Controller.getCurrentModeController().addAction(new HideWorkspace());
		Controller.getCurrentModeController().addAction(new SetWorkspaceLocation());
		
		Controller.getCurrentModeController().addAction(new CopyPhysicalNode());
		Controller.getCurrentModeController().addAction(new CutPhysicalNode());
		Controller.getCurrentModeController().addAction(new DeletePhysicalNode());
		Controller.getCurrentModeController().addAction(new PastePhysicalNode());
		Controller.getCurrentModeController().addAction(new RenamePhysicalNode());

		popupMap = new HashMap<String, PopupMenus.PopupObject>();

		// register Workspace PopupMenu
		registerPopupMenu(WORKSPACE_POPUP_MENU_KEY, WORKSPACE_POPUP_MENU_KEY, WORKSPACE_POPUP_MENU_CONFIG);
		registerPopupMenu(WORKSPACE_PHYSICAL_NODE_POPUP_MENU_KEY, WORKSPACE_PHYSICAL_NODE_POPUP_MENU_KEY,
				WORKSPACE_POPUP_MENU_CONFIG);
	}

	public boolean registerPopupMenu(final String key) {
		if (popupMap.containsKey(key)) {
			return false;
		}
		registerPopupMenu(key, WORKSPACE_NODE_POPUP_MENU_KEY, WORKSPACE_POPUP_MENU_CONFIG);
		return true;
	}

	public void registerPopupMenu(final String key, final String xmlKey, final String xmlFile) {
		if (!this.popupMap.containsKey(key)) {
			PopupObject popObj = new PopupObject(new JPopupMenu(), new MenuBuilder(Controller.getCurrentModeController()));

			this.popupMap.put(key, popObj);
			popObj.menuBuilder.addPopupMenu(popObj.popupMenu, xmlKey);

			final ControllerPopupMenuListener popupListener = new ControllerPopupMenuListener();
			popObj.popupMenu.addHierarchyListener(popupListener);

			Set<String> emptySet = Collections.emptySet();
			popObj.menuBuilder.processMenuCategory(this.getClass().getResource(xmlFile), emptySet);
		}
	}

	public void addAction(final String popupKey, final String menuKey, AFreeplaneAction action) {
		PopupObject popObj = this.popupMap.get(popupKey);
		popObj.menuBuilder.addAction(menuKey, action, MenuBuilder.AS_CHILD);
	}

	public void addCechkbox(final String popupKey, final String menuKey, AFreeplaneAction action, boolean isSelected) {
		PopupObject popObj = this.popupMap.get(popupKey);
		// if (!popObj.menuBuilder.contains(menuKey+"/"+action.getKey())) {
		popObj.menuBuilder.addCheckboxItem(menuKey, menuKey + "/" + action.getKey(), action, isSelected);
		// }
	}

	public class CheckBoxAction extends AFreeplaneAction {

		private static final long serialVersionUID = 1L;
		private String propertyKey;

		public CheckBoxAction(String key, String propertyKey) {
			super(key);
			this.propertyKey = propertyKey;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("PopupMenus.actionPerformed: " + e.getActionCommand() + " " + this.propertyKey);
			boolean checked = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			Controller.getCurrentController().getResourceController().setProperty(this.propertyKey, checked);

			// if (checked) {
			// WorkspaceController.getCurrentWorkspaceController().showWorkspaceView(true);
			// }
			// else {
			// WorkspaceController.getCurrentWorkspaceController().showWorkspaceView(false);
			// }
		}
	}

	public void showWorkspacePopup(Component component, int x, int y) {
		showPopup(WORKSPACE_POPUP_MENU_KEY, component, x, y);
	}

	public void showPhysicalNodePopup(Component component, int x, int y) {
		showPopup(WORKSPACE_PHYSICAL_NODE_POPUP_MENU_KEY, component, x, y);
	}

	public void showPopup(String popupKey, Component component, int x, int y) {
		PopupObject popObj = this.popupMap.get(popupKey);
		final JPopupMenu popupmenu = popObj.popupMenu;
		if (popupmenu != null) {
			popupmenu.show(component, x, y);
		}
	}

	private class PopupObject {
		public JPopupMenu popupMenu;
		public MenuBuilder menuBuilder;

		public PopupObject(final JPopupMenu popupMenu, final MenuBuilder menuBuilder) {
			this.menuBuilder = menuBuilder;
			this.popupMenu = popupMenu;
		}

	}
}