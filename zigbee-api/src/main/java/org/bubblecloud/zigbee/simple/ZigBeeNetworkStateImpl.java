package org.bubblecloud.zigbee.simple;

import java.util.*;

/**
 * The ZigBee network implementation. This class is thread safe.
 */
public class ZigBeeNetworkStateImpl implements ZigBeeNetworkState {
    /**
     * The devices in the ZigBee network.
     */
    private Map<Integer, ZigBeeDevice> devices = new TreeMap<Integer, ZigBeeDevice>();
    /**
     * The listeners of the ZigBee network.
     */
    private List<ZigBeeNetworkStateListener> listeners = Collections.unmodifiableList(
            new ArrayList<ZigBeeNetworkStateListener>());

    @Override
    public void addDevice(final ZigBeeDevice device) {
        synchronized (devices) {
            devices.put(device.getNetworkAddress(), device);
        }
        synchronized (this) {
            for (final ZigBeeNetworkStateListener listener : listeners) {
                listener.deviceAdded(device);
            }
        }
    }

    @Override
    public ZigBeeDevice getDevice(final int networkAddress) {
        synchronized (devices) {
            return devices.get(networkAddress);
        }
    }

    @Override
    public void removeDevice(final int networkAddress) {
        final ZigBeeDevice device;
        synchronized (devices) {
            device = devices.remove(networkAddress);
        }
        synchronized (this) {
            if (device != null) {
                for (final ZigBeeNetworkStateListener listener : listeners) {
                    listener.deviceRemoved(device);
                }
            }
        }
    }

    @Override
    public List<ZigBeeDevice> getDevices() {
        synchronized (devices) {
            return new ArrayList<ZigBeeDevice>(devices.values());
        }
    }

    @Override
    public void addNetworkListener(final ZigBeeNetworkStateListener networkListener) {
        synchronized (this) {
            final List<ZigBeeNetworkStateListener> modifiedListeners = new ArrayList<ZigBeeNetworkStateListener>(
                    listeners);
            modifiedListeners.add(networkListener);
            listeners = Collections.unmodifiableList(modifiedListeners);
        }
    }

    @Override
    public void removeNetworkListener(final ZigBeeNetworkStateListener networkListener) {
        synchronized (this) {
            final List<ZigBeeNetworkStateListener> modifiedListeners = new ArrayList<ZigBeeNetworkStateListener>(
                    listeners);
            modifiedListeners.remove(networkListener);
            listeners = Collections.unmodifiableList(modifiedListeners);
        }
    }

}