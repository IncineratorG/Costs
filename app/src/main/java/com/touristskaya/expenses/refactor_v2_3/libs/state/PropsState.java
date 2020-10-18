package com.touristskaya.expenses.refactor_v2_3.libs.state;

import com.touristskaya.expenses.refactor_v2_3.libs.selector.PropsSelector;
import com.touristskaya.expenses.refactor_v2_3.libs.state_prop.StatePropLike;
import com.touristskaya.expenses.refactor_v2_3.libs.void_function.VoidFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Add a class header comment
 */

public abstract class PropsState {
    private List<StatePropLike> mStateProps;
    private Map<Integer, List<PropsSelector>> mPropSelectorsMap;

    public PropsState() {
        mStateProps = new ArrayList<>();
        mPropSelectorsMap = new HashMap<>();
    }

    public VoidFunction select(PropsSelector selector) {
        List<Integer> selectorPropIds = selector.propIds();

        for (int i = 0; i < selectorPropIds.size(); ++i) {
            int selectorPropId = selectorPropIds.get(i);

            List<PropsSelector> selectorsList = mPropSelectorsMap.get(selectorPropId);
            if (selectorsList == null) {
                selectorsList = new ArrayList<>();
            }
            selectorsList.add(selector);
            mPropSelectorsMap.put(selectorPropId, selectorsList);
        }

        selector.selectorFunc().invoke();

        int selectorId = selector.id();

        return () -> {
            for (int selectorPropIndex = 0; selectorPropIndex < selectorPropIds.size(); ++selectorPropIndex) {
                int selectorPropId = selectorPropIds.get(selectorPropIndex);

                List<PropsSelector> selectorsList = mPropSelectorsMap.get(selectorPropId);
                if (selectorsList == null) {
                    return;
                }

                for (int selectorIndex = 0; selectorIndex < selectorsList.size(); ++selectorIndex) {
                    if (selectorsList.get(selectorIndex).id() == selectorId) {
                        selectorsList.remove(selectorIndex);
                        break;
                    }
                }
                if (selectorsList.size() <= 0) {
                    mPropSelectorsMap.remove(selectorPropId);
                } else {
                    mPropSelectorsMap.put(selectorPropId, selectorsList);
                }
            }
        };
    }

    public void update(VoidFunction updater) {
        updater.invoke();
        notifySelectors();
    }

    protected void initState() {
        List<StatePropLike> props = stateProps();
        for (int i = 0; i < props.size(); ++i) {
            props.get(i).setPropId(StatePropIdsGenerator.nextId());
        }
    }

    abstract protected List<StatePropLike> stateProps();

    private void notifySelectors() {
        Set<Integer> invokedSelectorIds = new HashSet<>();
        List<Integer> updatedPropIds = new ArrayList<>();
        List<StatePropLike> props = stateProps();

        for (int propIndex = 0; propIndex < props.size(); ++propIndex) {
            StatePropLike prop = props.get(propIndex);

            if (prop.updated()) {
                updatedPropIds.add(prop.propId());
                prop.setUpdated(false);
            }
        }

        for (int propId : updatedPropIds) {
            if (mPropSelectorsMap.containsKey(propId)) {
                List<PropsSelector> selectorsList = mPropSelectorsMap.get(propId);

                if (selectorsList != null) {
                    for (int selectorIndex = 0; selectorIndex < selectorsList.size(); ++selectorIndex) {
                        int selectorId = selectorsList.get(selectorIndex).id();
                        if (!invokedSelectorIds.contains(selectorId)) {
                            selectorsList.get(selectorIndex).selectorFunc().invoke();
                            invokedSelectorIds.add(selectorId);
                        }
                    }
                }
            }
        }
    }

//    private void notifySelectors() {
//        Set<Integer> invokedSelectorIds = new HashSet<>();
//
//        List<StatePropLike> props = stateProps();
//        for (int propIndex = 0; propIndex < props.size(); ++propIndex) {
//            StatePropLike prop = props.get(propIndex);
//            if (prop.updated()) {
//                int propId = prop.propId();
//
//                if (mPropSelectorsMap.containsKey(propId)) {
//                    List<PropsSelector> selectorsList = mPropSelectorsMap.get(propId);
////                    if (selectorsList == null) {
////                        continue;
////                    }
//
//                    if (selectorsList != null) {
//                        for (int selectorIndex = 0; selectorIndex < selectorsList.size(); ++selectorIndex) {
//                            int selectorId = selectorsList.get(selectorIndex).id();
//                            if (!invokedSelectorIds.contains(selectorId)) {
//                                selectorsList.get(selectorIndex).selectorFunc().invoke();
//                                invokedSelectorIds.add(selectorId);
//                            }
//                        }
//                    }
//                }
//
//                prop.setUpdated(false);
//            }
//
////            prop.setUpdated(false);
//        }
//
//        SystemEventsHandler.onInfo("\n");
//        List<StatePropLike> props2 = stateProps();
//        for (int propIndex = 0; propIndex < props.size(); ++propIndex) {
//            SystemEventsHandler.onInfo(props2.get(propIndex).propId() + " - " + props2.get(propIndex).updated());
//        }
//        SystemEventsHandler.onInfo("\n");
//    }

    // ===
//    public void update_V2(VoidFunction updater) {
//        updater.invoke();
//
//        List<StatePropLike> props = stateProps();
//        for (int propIndex = 0; propIndex < props.size(); ++propIndex) {
//            StatePropLike prop = props.get(propIndex);
//            if (prop.updated()) {
//                SystemEventsHandler.onInfo("UPDATED: " + prop.propId());
//
////                prop.setUpdated(false);
//            }
//        }
//    }
    // ===
}
