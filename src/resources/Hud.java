package resources;

import java.util.ArrayList;
import java.util.Random;

import engine.GameCode;
import engine.GameObject;
import engine.ObjectHandler;
import gameObjects.DataSlot;
import gameObjects.Register;
import map.Roome;

public class Hud extends GameObject {
	
	static long score = 0;
	static Textbox scoreDisplay;
	static long timeLeft = 60000 * 5;
	static int roundNum = 1;
	static Textbox timer;
	static Textbox waveNum;
	static Textbox registersRemaining;
	long prevTime;
	
	
	public Hud () {
		scoreDisplay = new Textbox ("SCORE: 00000000");
		scoreDisplay.changeHeight(2 * 16);
		scoreDisplay.changeWidth(17 * 16);
		scoreDisplay.setFont("text (lime green)");
		scoreDisplay.setBox("Green");
		
		timer = new Textbox (" ");
		timer.changeHeight(2 * 16);
		timer.changeWidth(22 * 16);
		timer.setFont("text (lime green)");
		timer.setBox("Green");
		
		registersRemaining = new Textbox (" ");
		registersRemaining.changeHeight(2 * 16);
		registersRemaining.changeWidth(22 * 16);
		registersRemaining.setFont("text (lime green)");
		registersRemaining.setBox("Green");
		
		waveNum = new Textbox ("WAVE NUMBER 1");
		waveNum.changeHeight(2 * 16);
		waveNum.changeWidth(22 * 16);
		waveNum.setFont("text (lime green)");
		waveNum.setBox("Green");
		
		this.setRenderPriority(5);
	}

	public static void updateScore (long change) {
		score = score + change;
		
		String workin = Long.toString(score);
		int padNum = 8 - workin.length();
		
		String finalString = "SCORE: ";
		for (int i = 0; i < padNum; i++) {
			finalString = finalString + "0";
		}
		finalString = finalString + workin;
		
	
		
		scoreDisplay.changeText(finalString);
	}
	
	@Override
	public void draw () {
		// once we do multiplayer put something here that make this happen only if its the right player
		scoreDisplay.setX(320 + GameCode.getViewX());
		scoreDisplay.setY(100 + GameCode.getViewY());
		scoreDisplay.draw();
		
		timer.setX(640 + GameCode.getViewX());
		timer.setY(60 + GameCode.getViewY());
		
		waveNum.setX(640 + GameCode.getViewX());
		waveNum.setY(20 + GameCode.getViewY());
		waveNum.draw();
		
		registersRemaining.setX(640 + GameCode.getViewX());
		registersRemaining.setY(100 + GameCode.getViewY());
		registersRemaining.changeText(Integer.toString(ObjectHandler.getObjectsByName("Register").size()) +" REGISTERS REMAIN");
		registersRemaining.draw();
		
		if (prevTime != 0) {
			timeLeft = timeLeft - (System.currentTimeMillis() - prevTime);
			if (timeLeft <= 0) {
				newWave();
			}
		}
		prevTime = System.currentTimeMillis();
		
		int numMinutes = (int) (timeLeft/60000);
		int numSeconds = (int) ((timeLeft - numMinutes * 60000)/1000);
		
		String secondsString = Integer.toString(numSeconds);
		
		if (secondsString.length() == 1) {
			secondsString = "0" + secondsString;
		}
		
		timer.changeText(Integer.toString(numMinutes) + ":"+ secondsString + " REMAINING");
		timer.draw();
		
	
	}
	
	
	public static void newWave() {
		roundNum = roundNum + 1;
		waveNum.changeText("WAVE NUMBER " + Integer.toString(roundNum));
		ArrayList<GameObject> slots = ObjectHandler.getObjectsByName("DataSlot");
		for (int i = 0; i < slots.size(); i++) {
			DataSlot currentSlot = (DataSlot) slots.get(i);
			if (currentSlot.isCleared()) {
				currentSlot.forget();
			}
		}
		Random rand = new Random ();
		for (int i = 0; i < Roome.map.length; i++) {
			for (int j = 0; j < Roome.map[i].length; j++) {
				if (rand.nextInt(20) < roundNum) {
					int memNum = rand.nextInt(256);
					
					Register r = new Register(memNum);
					
					int [] spawnPoint = Roome.map[i][j].biatch.getPosibleCoords(r.hitbox().width, r.hitbox().height);
					
					r.declare((int)spawnPoint[0], (int) spawnPoint[1]);
					
					Roome.map[i][j].r = r;
					
					
					Roome dataRoom = Roome.map [rand.nextInt(10)][rand.nextInt(10)];
					DataSlot ds = new DataSlot (memNum);
					
					int [] otherPoint = dataRoom.biatch.getPosibleCoords(ds.hitbox().width, ds.hitbox().height);
					
					
					ds.declare((int)otherPoint[0],(int) otherPoint[1]);
					
					dataRoom.ds = ds;
				}
			}
		}
		
		timeLeft = 60000 * 5 + 60000 * roundNum;
	}
	
}