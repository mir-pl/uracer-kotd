package com.bitfire.uracer.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bitfire.uracer.screen.ScreenFactory.ScreenType;
import com.bitfire.uracer.screen.transitions.ScreenTransition;

public final class TransitionManager {

	boolean paused, usedepth;
	Format fbFormat;
	FrameBuffer fbFrom, fbTo;
	ScreenTransition transition;

	public TransitionManager( boolean use32Bits, boolean useAlphaChannel, boolean useDepth ) {
		transition = null;
		paused = false;
		fbFormat = Format.RGB565;
		usedepth = useDepth;

		if( use32Bits ) {
			if( useAlphaChannel ) {
				fbFormat = Format.RGBA8888;
			} else {
				fbFormat = Format.RGB888;
			}
		} else {
			if( useAlphaChannel ) {
				fbFormat = Format.RGBA4444;
			} else {
				fbFormat = Format.RGB565;
			}
		}

		fbFrom = new FrameBuffer( fbFormat, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), useDepth );
		fbTo = new FrameBuffer( fbFormat, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), useDepth );
	}

	public void dispose() {
		fbFrom.dispose();
		fbTo.dispose();
	}

	/** Starts the specified transition */
	public void start( Screen curr, ScreenType next, ScreenTransition transition ) {
		removeTransition();
		this.transition = transition;

		// build source textures
		ScreenUtils.copyScreen( curr, fbFrom );

		// enable depth writing if its the case
		Gdx.gl20.glDepthMask( usedepth );
		this.transition.frameBuffersReady( curr, fbFrom, next, fbTo );
	}

	public boolean isActive() {
		return (transition != null);
	}

	public boolean isComplete() {
		return transition.isComplete();
	}

	public void removeTransition() {
		transition = null;
	}

	public ScreenTransition getTransition() {
		return transition;
	}

	public void pause() {
		if( paused ) {
			return;
		}

		paused = true;
		if( transition != null ) {
			transition.pause();
		}
	}

	public void resume() {
		if( !paused ) {
			return;
		}

		paused = false;
		if( transition != null ) {
			transition.resume();
		}
	}

	public void update() {
		if( paused ) {
			return;
		}

		if( transition != null ) {
			transition.update();
		}
	}

	public void render() {
		if( paused ) {
			return;
		}

		if( transition != null ) {
			transition.render();
		}
	}
}