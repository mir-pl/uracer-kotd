package com.bitfire.uracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bitfire.uracer.ScalingStrategy;
import com.bitfire.uracer.configuration.Config;
import com.bitfire.uracer.screen.ScreenFactory.ScreenType;
import com.bitfire.uracer.screen.TransitionFactory.TransitionType;
import com.bitfire.uracer.screen.transitions.ScreenTransition;

public final class ScreenManager {

	private ScalingStrategy strategy;
	private TransitionManager transMgr;
	private Screen current;
	private ScreenType next;
	private boolean quitPending, doSetScreenImmediate, justTransitioned;
	private GL20 gl;

	public ScreenManager( ScalingStrategy scalingStrategy ) {
		transMgr = new TransitionManager( Config.isDesktop /* 32bits */, false, true );
		strategy = scalingStrategy;
		current = null;
		next = ScreenType.NoScreen;
		quitPending = false;
		doSetScreenImmediate = false;
		justTransitioned = false;
		gl = Gdx.gl20;
	}

	public void dispose() {
		if( current != null ) {
			current.dispose();
			current = null;
		}

		transMgr.dispose();
	}

	public boolean begin() {
		if( quitPending ) {
			return false;
		}

		if( (transMgr.isActive() && transMgr.isComplete()) ) {
			current = transMgr.getTransition().nextScreen();
			next = ScreenType.NoScreen;
			transMgr.removeTransition();

			// switched to a null screen?
			if( current == null ) {
				quitPending = true;
				Gdx.app.log( "ScreenManager", "No screens available, bye!" );
				Gdx.app.exit();	// async exit
			}
		} else if( doSetScreenImmediate ) {
			doSetScreenImmediate = false;
			current = ScreenFactory.createScreen( next );
		}

		return true;
	}

	public void end() {
	}

	/** Switch to the screen identified by the specified screen type, using the
	 * specified transition type in its default
	 * configuration.
	 * The screen change is scheduled to happen at the start of the next frame. */
	public void setScreen( ScreenType screen, TransitionType transitionType, long transitionDurationMs ) {
		ScreenTransition transition = null;

		// if no transition or no duration avoid everything and pass a null
		// reference
		if( transitionType != TransitionType.None && transitionDurationMs > 0 ) {
			transition = TransitionFactory.getTransition( transitionType );
			transition.setDuration( transitionDurationMs );
		}

		setScreen( screen, transition );
	}

	/** Switch to the screen identified by the specified screen type, using the
	 * specified transition.
	 * The screen change is scheduled to happen at the start of the next frame. */
	public void setScreen( ScreenType screen, ScreenTransition transition ) {
		// early exit
		if( transMgr.isActive() ) {
			return;
		}

		doSetScreenImmediate = false;
//		Screen newScreen = ScreenFactory.createScreen( screen );
		next = screen;

		// if no transition then just setup a screen switch
		if( transition != null ) {
			transMgr.start( current, screen, transition );
		} else {
			doSetScreenImmediate = true;
		}

		// dispose the current screen
		if( current != null ) {
			Gdx.app.debug( "ScreenManager", "Destroying " + current.getClass().getSimpleName() );
			current.dispose();
			current = null;
			System.gc();
		}
	}

	public boolean quit() {
		return quitPending;
	}

	public void tick() {
		if( transMgr.isActive() ) {
			return;
		}

		if( current != null ) {
			current.tick();
		}
	}

	public void tickCompleted() {

		if( transMgr.isActive() ) {
			return;
		}

		if( current != null ) {
			current.tickCompleted();
		}
	}

	public void render( FrameBuffer dest ) {
		if( transMgr.isActive() ) {
			transMgr.update();
			transMgr.render();
			justTransitioned = true;
		} else {
			if( current != null ) {
				if( justTransitioned ) {
					justTransitioned = false;

					// ensures default active texture is active
					gl.glActiveTexture( GL20.GL_TEXTURE0 );
				}

				current.render( dest );
			}
		}
	}

	public void debugRender() {
		if( transMgr.isActive() ) {
			return;
		}

		if( current != null ) {
			current.debugRender();
		}
	}

	public void pause() {
		if( quitPending ) {
			return;
		}

		if( transMgr.isActive() ) {
			transMgr.pause();
		} else {
			if( current != null ) {
				current.pause();
			}
		}
	}

	public void resume() {
		if( quitPending ) {
			return;
		}

		if( transMgr.isActive() ) {
			transMgr.resume();
		} else {
			if( current != null ) {
				current.resume();
			}
		}
	}
}
