package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

class SystemKeyspaceChildFactory extends ChildFactory<KeyspaceMetadata> {

    private final Cluster cluster;
    private final Session session;

    public SystemKeyspaceChildFactory(Cluster cluster, Session session) {
        this.cluster = cluster;
        this.session = session;
    }

    @Override
    protected boolean createKeys(List<KeyspaceMetadata> list) {
        List<KeyspaceMetadata> keyspaces = cluster.getMetadata().getKeyspaces();
        for (KeyspaceMetadata keyspace : keyspaces) {
            if (keyspace.getName().equals("OpsCenter") || keyspace.getName().equals("system_traces") || (keyspace.getName().equals("system"))) {
                list.add(keyspace);
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(KeyspaceMetadata key) {
        SystemKeyspaceNode node = null;
        try {
            node = new SystemKeyspaceNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private class SystemKeyspaceNode extends BeanNode {
        public SystemKeyspaceNode(KeyspaceMetadata bean) throws IntrospectionException {
            super(bean, Children.create(new SystemTableChildFactory(bean, session), true), Lookups.singleton(bean));
            setDisplayName(bean.getName());
        }
    }

}
