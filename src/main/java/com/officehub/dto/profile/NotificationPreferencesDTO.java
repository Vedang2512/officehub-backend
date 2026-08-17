package com.officehub.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDTO {

    private boolean emailNotifications;

    private boolean taskNotifications;

    private boolean chatNotifications;

}