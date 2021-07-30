package menu;

import engine.GameObject;

public class MenuComponite extends GameObject{
	
	int width;
	int height;

	Menu menu;
	
	public MenuComponite (int width, int height, Menu m) {
		
		this.width = width;
		this.height = height;
		
		menu = m;
		m.addComponite(this);
	}
	
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) { 
		this.width = width;
	}

	public int getHeight() {
		return height;
	}
	
	public void compointeFrame () {
		
	}
	
	public void setHeight(int height) {
		menu.removeComponite(this);
		this.height = height;
		menu.addComponite(this);
	}

}
