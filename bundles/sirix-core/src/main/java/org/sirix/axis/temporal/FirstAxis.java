package org.sirix.axis.temporal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.sirix.api.NodeReadTrx;
import org.sirix.api.Session;
import org.sirix.exception.SirixException;
import org.sirix.utils.LogWrapper;
import org.slf4j.LoggerFactory;

import com.google.common.collect.AbstractIterator;

/**
 * Open the first revision and try to move to the node with the given node key.
 * 
 * @author Johannes Lichtenberger
 * 
 */
public class FirstAxis extends AbstractIterator<NodeReadTrx> {
	/** Logger. */
	private static final LogWrapper LOGGER = new LogWrapper(
			LoggerFactory.getLogger(AllTimeAxis.class));

	/** Sirix {@link Session}. */
	private final Session mSession;

	/** Node key to lookup and retrieve. */
	private long mNodeKey;

	/** Sirix {@link NodeReadTrx}. */
	private NodeReadTrx mRtx;

	/** Determines if it's the first call. */
	private boolean mFirst;

	/**
	 * Constructor.
	 * 
	 * @param session
	 *          {@link Sirix} session
	 * @param nodeKey
	 *          the key of the node to lookup in each revision
	 */
	public FirstAxis(final @Nonnull Session session,
			final @Nonnegative long nodeKey) {
		mSession = checkNotNull(session);
		checkArgument(nodeKey > -1, "nodeKey must be >= 0!");
		mNodeKey = nodeKey;
		mFirst = true;
	}

	@Override
	protected NodeReadTrx computeNext() {
		if (mFirst) {
			mFirst = false;
			try {
				mRtx = mSession.beginNodeReadTrx(0);
			} catch (final SirixException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return mRtx.moveTo(mNodeKey).hasMoved() ? mRtx : endOfData();
		} else {
			return endOfData();
		}
	}
}