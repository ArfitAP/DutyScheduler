export interface IRoom {
    id: number;
    name: string;
    description: string;
    ownerUsername: string;
    ownerId: number;
    memberCount: number;
    createdAt: string;
    isOwner: boolean;
}

export interface IRoomDetail {
    id: number;
    name: string;
    description: string;
    ownerUsername: string;
    ownerId: number;
    memberCount: number;
    createdAt: string;
    isOwner: boolean;
    members: IRoomMember[];
}

export interface IRoomMember {
    id: number;
    username: string;
}

export interface IRoomInvitation {
    id: number;
    roomId: number;
    roomName: string;
    invitedByUsername: string;
    status: string;
    createdAt: string;
}

export interface IRoomUser {
    id: number;
    username: string;
}
