/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.config;

import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationSettings<_H extends Header, _O extends Operation, _B extends Binding> {

    static String OPERATION_DEFAULT_SETTINGS = "operation-basic-settings";
    static String BINDING_DEFAULT_SETTINGS = "binding-basic-settings";

    private List<HeaderProperties> headers;
    private List<OperationProperties> operations;
    private List<BindingProperties> bindings;

    protected List<HeaderProperties> getHeaders() {
        return headers;
    }

    public void setHeaders(List<HeaderProperties> headers) {
        this.headers = headers;
    }

    public HeaderProperties getHeader(_H header) {
        if (header == null || headers == null)
            return null;
        String configName = header.getConfigName();
        for (HeaderProperties headerProps : headers) {
            if (configName.equals(headerProps.getName()))
                return headerProps;
        }
        return null;
    }

    protected List<OperationProperties> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationProperties> operations) {
        this.operations = operations == null ? new ArrayList<>(0) : operations;
    }

    public OperationProperties getOperation(_O operation) {
        return getOperation(operation.getConfigName());
    }

    protected OperationProperties getOperation(String operation) {
        if (operation == null || operations == null)
            return null;
        for (OperationProperties operationProps : operations) {
            if (operation.equals(operationProps.getName()))
                return operationProps;
        }
        return null;
    }

    public TenantProperties getOperation(_O operation, String tenant) {
        return getOperation(operation.getConfigName(), tenant);
    }

    protected TenantProperties getOperation(String operation, String tenant) {
        if (operation == null || operations == null)
            return null;
        for (OperationProperties operationProps : operations) {
            if (operation.equals(operationProps.getName()))
                return operationProps.getTenant(tenant);
        }
        return null;
    }

    public List<BindingProperties> getBindings() {
        return bindings;
    }

    public void setBindings(List<BindingProperties> bindings) {
        this.bindings = bindings == null ? new ArrayList<>(0) : bindings;
    }

    public BindingProperties getBinding(_B binding) {
        return getBinding(binding.getConfigName());
    }

    protected BindingProperties getBinding(String binding) {
        if (binding == null || bindings == null)
            return null;
        for (BindingProperties bindingProps : bindings) {
            if (binding.equals(bindingProps.getName()))
                return bindingProps;
        }
        return null;
    }

    public TenantProperties getBinding(_B binding, String tenant) {
        return getBinding(binding.getConfigName(), tenant);
    }

    protected TenantProperties getBinding(String binding, String tenant) {
        if (binding == null || bindings == null)
            return null;
        for (BindingProperties bindingProps : bindings) {
            if (binding.equals(bindingProps.getName()))
                return bindingProps.getTenant(tenant);
        }
        return null;
    }

    protected void postConstruct(HubSettings parentSettings) {
        if (operations == null)
            operations = new ArrayList<>(0);
        else {
            OperationProperties defaultOperation = getOperation(OPERATION_DEFAULT_SETTINGS);
            for (OperationProperties operation : operations) {
                if (operation.isDefault())
                    continue;
                if (parentSettings != null) {
                    for (String tenant : parentSettings.getTenants()) {
                        if (operation.getTenant(tenant) == null)
                            operation.addTenant(new TenantProperties(tenant));
                    }
                }
                operation.postConstruct(defaultOperation);
            }
            operations.remove(defaultOperation);
        }

        if (bindings == null)
            bindings = new ArrayList<>(0);
        else {
            BindingProperties defaultBinding = getBinding(BINDING_DEFAULT_SETTINGS);
            for (BindingProperties binding : bindings) {
                if (binding.isDefault())
                    continue;
                if (parentSettings != null) {
                    for (String tenant : parentSettings.getTenants()) {
                        if (binding.getTenant(tenant) == null)
                            binding.addTenant(new TenantProperties(tenant));
                    }
                }
                binding.postConstruct(defaultBinding);
            }
            bindings.remove(defaultBinding);
        }
    }
}
