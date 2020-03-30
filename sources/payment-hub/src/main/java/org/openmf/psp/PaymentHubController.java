/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp;

//@RestController
public class PaymentHubController {

    private static final String[] PETS = new String[]{"Snoopy", "Fido", "Tony the Tiger"};

    /*@GetMapping(value = "/pets/{id}")
    public Map<String, String> petById(@PathVariable("id") Integer id) {
        if (id != null && id > 0 && id <= PETS.length + 1) {
            int index = id - 1;
            String pet = PETS[index];
            return Collections.singletonMap("name", pet);
        } else {
            return Collections.emptyMap();
        }

    }*/
}