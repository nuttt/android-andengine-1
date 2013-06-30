package com.tutorial.towerofhanoi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.util.Log;

public class MainActivity extends SimpleBaseGameActivity {
	
	private static int CAMERA_WIDTH = 800;
	private static int CAMERA_HEIGHT = 480;
	
	private static int count = 0;
	private ITextureRegion mBackgroundTextureRegion, mTowerTextureRegion, mRing0, mRing1, mRing2, mRing3;
	
	private Sprite mTower1, mTower2, mTower3;
	
	private Stack mStack1, mStack2, mStack3;
	private Font mFont;
	private Font mFont2;
	private Text moveText;
	private Text winText;
	private SimpleBaseGameActivity activity;

	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		activity = this;
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		try {
		    // 1 - Set up bitmap textures
		    ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/background.png");
		        }
		    });
		    ITexture towerTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/tower.png");
		        }
		    });
		    ITexture ring0 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ring0.png");
		        }
		    });
		    ITexture ring1 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ring1.png");
		        }
		    });
		    ITexture ring2 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ring2.png");
		        }
		    });
		    ITexture ring3 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ring3.png");
		        }
		    });
		    // 2 - Load bitmap textures into VRAM
		    backgroundTexture.load();
		    towerTexture.load();
		    ring0.load();
		    ring1.load();
		    ring2.load();
		    ring3.load();
		    
		    // 3 - Set up texture regions
		    this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture);
		    this.mTowerTextureRegion = TextureRegionFactory.extractFromTexture(towerTexture);
		    this.mRing0 = TextureRegionFactory.extractFromTexture(ring0);
		    this.mRing1 = TextureRegionFactory.extractFromTexture(ring1);
		    this.mRing2 = TextureRegionFactory.extractFromTexture(ring2);
		    this.mRing3 = TextureRegionFactory.extractFromTexture(ring3);
		    
		    // 4 - Create the stacks
		    this.mStack1 = new Stack();
		    this.mStack2 = new Stack();
		    this.mStack3 = new Stack();
		    
		    // Load font
		    this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		    this.mFont2 = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 96);
			this.mFont.load();
			this.mFont2.load();
		    
		} catch (IOException e) {
		    Debug.e(e);
		}
		
	}

	@Override
	protected Scene onCreateScene() {
		// TODO Auto-generated method stub
		// 1 - Create new scene
		final Scene scene = new Scene();
		Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, getVertexBufferObjectManager());
		scene.attachChild(backgroundSprite);
		
		// 2 - Add the towers
		mTower1 = new Sprite(192, 63, this.mTowerTextureRegion, getVertexBufferObjectManager());
		mTower2 = new Sprite(400, 63, this.mTowerTextureRegion, getVertexBufferObjectManager());
		mTower3 = new Sprite(604, 63, this.mTowerTextureRegion, getVertexBufferObjectManager());
		scene.attachChild(mTower1);
		scene.attachChild(mTower2);
		scene.attachChild(mTower3);
		
		// 3 - Create the rings
		Ring ring0 = new Ring(0, 149, 150, this.mRing0, getVertexBufferObjectManager()) {
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
		            return false;
		        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
		            pSceneTouchEvent.getY() - this.getHeight() / 2);
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		            checkForCollisionsWithTowers(this);
		        }
		        return true;
		    }
		};
		Ring ring1 = new Ring(1, 139, 174, this.mRing1, getVertexBufferObjectManager()) {
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
		            return false;
		        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
		            pSceneTouchEvent.getY() - this.getHeight() / 2);
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		            checkForCollisionsWithTowers(this);
		        }
		        return true;
		    }
		};
		Ring ring2 = new Ring(2, 118, 212, this.mRing2, getVertexBufferObjectManager()) {
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
		            return false;
		        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
		            pSceneTouchEvent.getY() - this.getHeight() / 2);
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		            checkForCollisionsWithTowers(this);
		        }
		        return true;
		    }
		};
		Ring ring3 = new Ring(3, 97, 255, this.mRing3, getVertexBufferObjectManager()) {
		    @Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
		            return false;
		        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, 
		            pSceneTouchEvent.getY() - this.getHeight() / 2);
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		            checkForCollisionsWithTowers(this);
		        }
		        return true;
		    }
		};
		scene.attachChild(ring0);
		scene.attachChild(ring1);
		scene.attachChild(ring2);
		scene.attachChild(ring3);
		
		// 4 - Add all rings to stack one
		this.mStack1.add(ring3);
		this.mStack1.add(ring2);
		this.mStack1.add(ring1);
		this.mStack1.add(ring0);
		// 5 - Initialize starting position for each ring
		ring0.setmStack(mStack1);
		ring1.setmStack(mStack1);
		ring2.setmStack(mStack1);
		ring3.setmStack(mStack1);
		ring0.setmTower(mTower1);
		ring1.setmTower(mTower1);
		ring2.setmTower(mTower1);
		ring3.setmTower(mTower1);
		// 6 - Add touch handlers
		scene.registerTouchArea(ring0);
		scene.registerTouchArea(ring1);
		scene.registerTouchArea(ring2);
		scene.registerTouchArea(ring3);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		// Move count
		this.moveText = new Text(50, 30, this.mFont, "move:" + count + "     ", new TextOptions(HorizontalAlign.CENTER), getVertexBufferObjectManager());
		scene.attachChild(moveText);
		
		// Winning Text
		this.winText = new Text(210, 300, this.mFont2, "                 ", new TextOptions(HorizontalAlign.CENTER), getVertexBufferObjectManager());
		this.winText.setColor(new Color(255, 255, 255));
		scene.attachChild(winText);
		return scene;
	}
	
	private void checkForCollisionsWithTowers(Ring ring) {
		Stack oldStack = ring.getmStack();
	    Stack stack = null;
	    Sprite tower = null;
	    if (ring.collidesWith(mTower1) && (mStack1.size() == 0 ||             
	            ring.getmWeight() < ((Ring) mStack1.peek()).getmWeight())) {
	        stack = mStack1;
	        tower = mTower1;
	    } else if (ring.collidesWith(mTower2) && (mStack2.size() == 0 || 
	            ring.getmWeight() < ((Ring) mStack2.peek()).getmWeight())) {
	        stack = mStack2;
	        tower = mTower2;
	    } else if (ring.collidesWith(mTower3) && (mStack3.size() == 0 || 
	            ring.getmWeight() < ((Ring) mStack3.peek()).getmWeight())) {
	        stack = mStack3;
	        tower = mTower3;
	    } else {
	        stack = ring.getmStack();
	        tower = ring.getmTower();
	    }
	    ring.getmStack().remove(ring);
	    if (stack != null && tower !=null && stack.size() == 0) {
	        ring.setPosition(tower.getX() + tower.getWidth()/2 - 
	            ring.getWidth()/2, tower.getY() + tower.getHeight() - 
	            ring.getHeight());
	    } else if (stack != null && tower !=null && stack.size() > 0) {
	        ring.setPosition(tower.getX() + tower.getWidth()/2 - 
	            ring.getWidth()/2, ((Ring) stack.peek()).getY() - 
	            ring.getHeight());
	    }
	    stack.add(ring);
	    if(stack != oldStack){
	    	count++;
	    	Log.d("count", ""+count);
	    	this.moveText.setText("move: " + count);
	    }
	    if(mStack3.size() >= 4){
	    	this.winText.setText("You Win!!");
	    }
	    ring.setmStack(stack);
	    ring.setmTower(tower);
	}

}
